package com.shodhAI.ShodhAI.Service;

//import com.shodhAI.ShodhAI.Entity.DeliveryStatus;
import com.shodhAI.ShodhAI.Entity.DeliveryStatus;
import com.shodhAI.ShodhAI.Entity.Faculty;
import com.shodhAI.ShodhAI.Entity.Notification;
import com.shodhAI.ShodhAI.Entity.NotificationRecipient;
import com.shodhAI.ShodhAI.Entity.NotificationType;
import com.shodhAI.ShodhAI.Entity.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class NotificationService {


    private static final Logger logger = Logger.getLogger(NotificationService.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    @Qualifier("blMailSender")
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Transactional
    public Notification createNotification(String title, String message, Long senderId, List<Long> notificationTypeIds, Date scheduledDate) {
        // Fetch notification types by IDs
        List<NotificationType> notificationTypes = entityManager.createQuery(
                        "SELECT nt FROM NotificationType nt WHERE nt.id IN :ids", NotificationType.class)
                .setParameter("ids", notificationTypeIds)
                .getResultList();

        if (notificationTypes.isEmpty()) {
            throw new RuntimeException("Invalid notification type IDs provided.");
        }

        // Fetch sender (faculty)
        Faculty sender = entityManager.find(Faculty.class, senderId);
        if (sender == null) {
            throw new RuntimeException("Faculty not found with ID: " + senderId);
        }

        // Create notification
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setSender(sender);
        notification.setNotificationTypes(notificationTypes);
        notification.setScheduledDate(scheduledDate);
        notification.setCreatedDate(new Date());
        notification.setIsSent(false);

        // Persist notification
        entityManager.persist(notification);

        // Important: Explicitly flush to ensure the notification_notification_type join table is populated
        entityManager.flush();

        return notification;
    }

    @Transactional
    public void sendNotificationToClass(Long classId, Notification notification) {
        Class classEntity = entityManager.find(Class.class, classId);
        if (classEntity == null) {
            throw new RuntimeException("Class not found with ID: " + classId);
        }

        // Get students in class
        TypedQuery<ClassStudent> query = entityManager.createQuery(
                "SELECT cs FROM ClassStudent cs WHERE cs.classEntity.id = :classId AND cs.archived = 'N'",
                ClassStudent.class
        );
        query.setParameter("classId", classId);
        List<ClassStudent> classStudents = query.getResultList();

        // Get pending delivery status
        DeliveryStatus pendingStatus = entityManager.find(DeliveryStatus.class, 1L);
        if (pendingStatus == null) {
            throw new RuntimeException("Pending delivery status not found");
        }

        // Create notification recipients
        List<NotificationRecipient> recipients = new ArrayList<>();
        for (ClassStudent classStudent : classStudents) {
            NotificationRecipient recipient = new NotificationRecipient();
            recipient.setNotification(notification);
            recipient.setStudent(classStudent.getStudent());
            recipient.setIsRead(false);
            recipient.setDeliveryStatus(pendingStatus);

            entityManager.persist(recipient);
            recipients.add(recipient);
        }

        // Associate recipients with notification (this is what was missing)
        notification.setRecipients(recipients);

        // Process notifications
        processNotifications(notification, recipients);

        // Refresh the notification entity to ensure it has the latest data
        entityManager.refresh(notification);
    }

    @Transactional
    public Notification getNotificationWithRecipients(Long notificationId) {
        TypedQuery<Notification> query = entityManager.createQuery(
                "SELECT n FROM Notification n LEFT JOIN FETCH n.recipients WHERE n.id = :id",
                Notification.class
        );
        query.setParameter("id", notificationId);
        return query.getSingleResult();
    }
    @Transactional
    public void processNotifications(Notification notification, List<NotificationRecipient> recipients) {
        // Mark notification as sent
        notification.setIsSent(true);
        entityManager.merge(notification);

        // Get delivery statuses by ID
        DeliveryStatus deliveredStatus = entityManager.find(DeliveryStatus.class, 2L);
        DeliveryStatus failedStatus = entityManager.find(DeliveryStatus.class, 3L);

        if (deliveredStatus == null || failedStatus == null) {
            throw new RuntimeException("Required delivery statuses not found");
        }

        // Extract all notification type codes
        List<String> notificationTypeCodes = notification.getNotificationTypes()
                .stream()
                .map(NotificationType::getCode)
                .collect(Collectors.toList());

        try {
            // Process each notification type
            for (String notificationTypeCode : notificationTypeCodes) {
                switch (notificationTypeCode) {
                    case "EMAIL":
                        sendBatchEmails(recipients, notification);
                        break;
                    case "SMS":
                        sendBatchSms(recipients, notification);
                        break;
                    case "APP":
                        // In-app notifications are already created
                        break;
                    case "ALL":
                        sendBatchEmails(recipients, notification);
                        sendBatchSms(recipients, notification);
                        break;
                    default:
                        logger.warning("Unknown notification type: " + notificationTypeCode);
                        break;
                }
            }

            // Mark recipients as delivered
            updateRecipientsStatus(recipients, deliveredStatus);

        } catch (Exception e) {
            logger.severe("Error processing notifications: " + e.getMessage());
            updateRecipientsStatus(recipients, failedStatus);
            throw new RuntimeException("Failed to process notifications", e);
        }
    }

    private void updateRecipientsStatus(List<NotificationRecipient> recipients, DeliveryStatus status) {
        for (NotificationRecipient recipient : recipients) {
            recipient.setDeliveryStatus(status);
            entityManager.merge(recipient);
        }
    }

    private void sendBatchEmails(List<NotificationRecipient> recipients, Notification notification) {
        try {
            // Extract email addresses from recipients
            List<String> emailAddresses = recipients.stream()
                    .map(r -> r.getStudent().getCollegeEmail())
                    .collect(Collectors.toList());

            // Send batch email
            sendEmailWithAttachments(
                    emailAddresses,
                    notification.getTitle(),
                    notification.getMessage(),
                    null // No attachments
            );

            logger.info("Batch email sent to " + emailAddresses.size() + " recipients");
        } catch (MessagingException e) {
            logger.severe("Failed to send batch email: " + e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }

    private void sendBatchSms(List<NotificationRecipient> recipients, Notification notification) {
        // Implementation for SMS batch sending
        logger.info("Batch SMS would be sent to " + recipients.size() + " recipients");
    }

    public void sendEmailWithAttachments(List<String> recipients, String subject, String content, List<File> attachments) throws MessagingException {
        if (recipients == null || recipients.isEmpty()) {
            throw new MessagingException("Recipients list cannot be empty");
        }

        String[] recipientArray = recipients.toArray(new String[0]);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setBcc(recipientArray);

            // Set empty string if subject is null
            helper.setSubject(subject == null ? "" : subject);

            // Set empty string if content is null
            helper.setText(content == null ? "" : content);

            // Add attachments
            if (attachments != null) {
                for (File file : attachments) {
                    if (file != null && file.exists()) {
                        helper.addAttachment(file.getName(), file);
                    }
                }
            }

            mailSender.send(message);
            System.out.println("Email Sent");
        } catch (MessagingException e) {
            throw new MessagingException("Failed to send email: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void sendNotificationToStudents(List<Long> studentIds, Notification notification) {
        // Get pending delivery status
        DeliveryStatus pendingStatus = entityManager.find(DeliveryStatus.class, 1L);
        if (pendingStatus == null) {
            throw new RuntimeException("Pending delivery status not found");
        }

        // Create notification recipients
        List<NotificationRecipient> recipients = new ArrayList<>();
        for (Long studentId : studentIds) {
            Student student = entityManager.find(Student.class, studentId);
            if (student == null) {
                logger.warning("Student not found with ID: " + studentId);
                continue;
            }

            NotificationRecipient recipient = new NotificationRecipient();
            recipient.setNotification(notification);
            recipient.setStudent(student);
            recipient.setIsRead(false);
            recipient.setDeliveryStatus(pendingStatus);

            entityManager.persist(recipient);
            recipients.add(recipient);
        }

        // Process notifications in batch
        processNotifications(notification, recipients);
    }



    @Transactional
    public List<Notification> getNotificationsBySender(Long facultyId) {
        TypedQuery<Notification> query = entityManager.createQuery(
                "SELECT n FROM Notification n WHERE n.sender.id = :facultyId AND n.archived = 'N' ORDER BY n.createdDate DESC",
                Notification.class
        );
        query.setParameter("facultyId", facultyId);
        return query.getResultList();
    }

    @Transactional
    public List<Notification> getNotificationsForStudent(Long studentId) {
        TypedQuery<NotificationRecipient> query = entityManager.createQuery(
                "SELECT nr FROM NotificationRecipient nr " +
                        "WHERE nr.student.id = :studentId AND nr.archived = 'N' " +
                        "ORDER BY nr.notification.createdDate DESC",
                NotificationRecipient.class
        );
        query.setParameter("studentId", studentId);
        List<NotificationRecipient> recipients = query.getResultList();

        List<Notification> notifications = new ArrayList<>();
        for (NotificationRecipient recipient : recipients) {
            notifications.add(recipient.getNotification());
        }

        return notifications;
    }

    @Transactional
    public void markNotificationAsRead(Long notificationId, Long studentId) {
        TypedQuery<NotificationRecipient> query = entityManager.createQuery(
                "SELECT nr FROM NotificationRecipient nr " +
                        "WHERE nr.notification.id = :notificationId AND nr.student.id = :studentId",
                NotificationRecipient.class
        );
        query.setParameter("notificationId", notificationId);
        query.setParameter("studentId", studentId);

        List<NotificationRecipient> recipients = query.getResultList();
        if (!recipients.isEmpty()) {
            NotificationRecipient recipient = recipients.get(0);
            recipient.setIsRead(true);
            recipient.setReadDate(new Date());
            entityManager.merge(recipient);
        }
    }
}