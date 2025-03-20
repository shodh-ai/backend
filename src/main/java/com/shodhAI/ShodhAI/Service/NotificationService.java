package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Entity.*;
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
    public void sendNotificationToCourse(Long courseId, Notification notification) {
        Course course = entityManager.find(Course.class, courseId);
        if (course == null) {
            throw new RuntimeException("Course not found with ID: " + courseId);
        }

        // Get faculty members associated with this course
        List<Faculty> facultyMembers = course.getFacultyMembers();
        if (facultyMembers == null || facultyMembers.isEmpty()) {
            throw new RuntimeException("No faculty members associated with course ID: " + courseId);
        }

        // Get all students associated with these faculty members
        List<Student> students = new ArrayList<>();
        for (Faculty faculty : facultyMembers) {
            List<Student> facultyStudents = faculty.getStudents();
            if (facultyStudents != null && !facultyStudents.isEmpty()) {
                students.addAll(facultyStudents);
            }
        }

        // Remove duplicates (if a student has multiple faculty members from the course)
        students = students.stream().distinct().collect(Collectors.toList());

        if (students.isEmpty()) {
            throw new RuntimeException("No students found for course ID: " + courseId);
        }

        // Set course relationship and mark as course announcement
        notification.setCourse(course);
        notification.setIsCourseAnnouncement(true);
        entityManager.merge(notification);

        // Create notification recipients
        List<NotificationRecipient> recipients = new ArrayList<>();
        for (Student student : students) {
            NotificationRecipient recipient = new NotificationRecipient();
            recipient.setNotification(notification);
            recipient.setRecipient(student);
            recipient.setReadStatus(false);

            // Find the pending delivery status
            DeliveryStatus pendingStatus = entityManager.createQuery(
                            "SELECT ds FROM DeliveryStatus ds WHERE ds.code = :code", DeliveryStatus.class)
                    .setParameter("code", "PENDING")
                    .getSingleResult();

            recipient.setDeliveryStatus(pendingStatus);

            entityManager.persist(recipient);
            recipients.add(recipient);
        }

        // Associate recipients with notification
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

        // Extract all notification type names
        List<String> notificationTypeNames = notification.getNotificationTypes()
                .stream()
                .map(NotificationType::getTypeName)
                .collect(Collectors.toList());

        try {
            // Process each notification type
            for (String typeName : notificationTypeNames) {
                switch (typeName) {
                    case "EMAIL":
                        sendBatchEmails(recipients, notification);
                        break;
                    case "SMS":
                        sendBatchSms(recipients, notification);
                        break;
                    case "IN_APP":
                        // In-app notifications are already created
                        break;
                    case "ALL":
                        sendBatchEmails(recipients, notification);
                        sendBatchSms(recipients, notification);
                        break;
                    default:
                        logger.warning("Unknown notification type: " + typeName);
                        break;
                }
            }

            // Mark recipients as delivered
            updateRecipientsStatus(recipients, "DELIVERED");

        } catch (Exception e) {
            logger.severe("Error processing notifications: " + e.getMessage());
            updateRecipientsStatus(recipients, "FAILED");
            throw new RuntimeException("Failed to process notifications", e);
        }
    }

    private void updateRecipientsStatus(List<NotificationRecipient> recipients, String statusCode) {
        // Find the delivery status by code
        DeliveryStatus status = entityManager.createQuery(
                        "SELECT ds FROM DeliveryStatus ds WHERE ds.code = :code", DeliveryStatus.class)
                .setParameter("code", statusCode)
                .getSingleResult();

        Date now = new Date();
        for (NotificationRecipient recipient : recipients) {
            recipient.setDeliveryStatus(status);
            if ("DELIVERED".equals(statusCode)) {
                recipient.setReadDate(now); // Using readDate as delivery date
            }
            entityManager.merge(recipient);
        }
    }

    private void sendBatchEmails(List<NotificationRecipient> recipients, Notification notification) {
        try {
            // Extract email addresses from recipients
            List<String> emailAddresses = recipients.stream()
                    .map(r -> r.getRecipient().getCollegeEmail())
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
        // Create notification recipients
        List<NotificationRecipient> recipients = new ArrayList<>();

        // Find the pending delivery status
        DeliveryStatus pendingStatus = entityManager.createQuery(
                        "SELECT ds FROM DeliveryStatus ds WHERE ds.code = :code", DeliveryStatus.class)
                .setParameter("code", "PENDING")
                .getSingleResult();

        for (Long studentId : studentIds) {
            Student student = entityManager.find(Student.class, studentId);
            if (student == null) {
                logger.warning("Student not found with ID: " + studentId);
                continue;
            }

            NotificationRecipient recipient = new NotificationRecipient();
            recipient.setNotification(notification);
            recipient.setRecipient(student);
            recipient.setReadStatus(false);
            recipient.setDeliveryStatus(pendingStatus);

            entityManager.persist(recipient);
            recipients.add(recipient);
        }

        // Associate recipients with notification
        notification.setRecipients(recipients);
        entityManager.merge(notification);

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
                        "WHERE nr.recipient.id = :studentId AND nr.notification.archived = 'N' " +
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
                        "WHERE nr.notification.id = :notificationId AND nr.recipient.id = :studentId",
                NotificationRecipient.class
        );
        query.setParameter("notificationId", notificationId);
        query.setParameter("studentId", studentId);

        List<NotificationRecipient> recipients = query.getResultList();
        if (!recipients.isEmpty()) {
            NotificationRecipient recipient = recipients.get(0);
            recipient.setReadStatus(true);
            recipient.setReadDate(new Date());
            entityManager.merge(recipient);
        }
    }

    @Transactional
    public void sendCourseAnnouncement(Long courseId, Notification notification) {
        // Get the course
        Course course = entityManager.find(Course.class, courseId);
        if (course == null) {
            throw new RuntimeException("Course not found with ID: " + courseId);
        }

        // Set course relationship and mark as course announcement
        notification.setCourse(course);
        notification.setIsCourseAnnouncement(true);
        entityManager.merge(notification);

        // Find all students associated with the course through faculty members
        List<Student> students = new ArrayList<>();
        for (Faculty faculty : course.getFacultyMembers()) {
            students.addAll(faculty.getStudents());
        }

        // Remove duplicates
        students = students.stream().distinct().collect(Collectors.toList());

        if (students.isEmpty()) {
            throw new RuntimeException("No students found for course ID: " + courseId);
        }

        // Create notification recipients
        List<NotificationRecipient> recipients = new ArrayList<>();

        // Find the pending delivery status
        DeliveryStatus pendingStatus = entityManager.createQuery(
                        "SELECT ds FROM DeliveryStatus ds WHERE ds.code = :code", DeliveryStatus.class)
                .setParameter("code", "PENDING")
                .getSingleResult();

        for (Student student : students) {
            NotificationRecipient recipient = new NotificationRecipient();
            recipient.setNotification(notification);
            recipient.setRecipient(student);
            recipient.setReadStatus(false);
            recipient.setDeliveryStatus(pendingStatus);

            entityManager.persist(recipient);
            recipients.add(recipient);
        }

        // Associate recipients with notification
        notification.setRecipients(recipients);
        entityManager.merge(notification);

        // Process notifications in batch
        processNotifications(notification, recipients);
    }
}