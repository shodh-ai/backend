package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.DeliveryStatus;
import com.shodhAI.ShodhAI.Entity.Faculty;
import com.shodhAI.ShodhAI.Entity.Notification;
import com.shodhAI.ShodhAI.Entity.NotificationRecipient;
import com.shodhAI.ShodhAI.Entity.NotificationType;
import com.shodhAI.ShodhAI.Entity.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private ExceptionHandlingService exceptionHandlingService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Transactional
    public Notification createNotification(String title, String message, Long senderId, List<Long> notificationTypeIds, Date scheduledDate) {
        if(message==null || message.trim().isEmpty())
        {
            throw new IllegalArgumentException("Message cannot be null or empty ");
        }
        if(notificationTypeIds==null || notificationTypeIds.isEmpty())
        {
            throw new IllegalArgumentException("Notification type ids cannot be null or empty");
        }
        // Fetch all existing NotificationType IDs from the database
        List<Long> existingNotificationTypeIds = entityManager.createQuery(
                        "SELECT nt.id FROM NotificationType nt", Long.class)
                .getResultList();

        if (existingNotificationTypeIds == null || existingNotificationTypeIds.isEmpty()) {
            throw new IllegalArgumentException("Notification type list is empty");
        }

        for (Long requestedId : notificationTypeIds) {
            if (!existingNotificationTypeIds.contains(requestedId)) {
                throw new IllegalArgumentException("Invalid Notification Type ID: " + requestedId);
            }
        }

        List<NotificationType> notificationTypes = entityManager.createQuery(
                        "SELECT nt FROM NotificationType nt WHERE nt.id IN :ids", NotificationType.class)
                .setParameter("ids", notificationTypeIds)
                .getResultList();

        if (notificationTypes.size() != notificationTypeIds.size()) {
            throw new IllegalArgumentException("One or more Notification Type IDs are invalid.");
        }

        Faculty sender = entityManager.find(Faculty.class, senderId);
        if (sender == null) {
            throw new IllegalArgumentException("Faculty not found with ID: " + senderId);
        }

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setSender(sender);
        notification.setNotificationTypes(notificationTypes);
        notification.setNotificationTimestamp(LocalDateTime.now());
        notification.setIsSent(false);
        entityManager.persist(notification);
        entityManager.flush();
        entityManager.refresh(notification);
        return notification;
    }

    @Transactional
    public void sendNotificationToCourse(Long courseId, Notification notification) {
        Course course = entityManager.find(Course.class, courseId);
        if (course == null) {
            throw new IllegalArgumentException("Course not found with ID: " + courseId);
        }

        // Get faculty members associated with this course
        List<Faculty> facultyMembers = course.getFacultyMembers();
        if (facultyMembers == null || facultyMembers.isEmpty()) {
            throw new IllegalArgumentException("No faculty members associated with course ID: " + courseId);
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
            throw new IllegalArgumentException("No students found for course ID: " + courseId);
        }

        notification.setCourse(course);
        notification.setIsCourseAnnouncement(true);
        notification = entityManager.merge(notification);

        entityManager.flush();

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
        entityManager.merge(notification);

        // Process notifications
        processNotifications(notification, recipients);

        // Refresh the notification entity to ensure it has the latest data
        entityManager.refresh(notification);
    }


    @Transactional(readOnly = true)
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
        entityManager.flush();

        List<Long> notificationTypeIds = notification.getNotificationTypes()
                .stream()
                .filter(nt -> nt != null && nt.getId() != null)
                .map(NotificationType::getId)
                .collect(Collectors.toList());

        boolean success = false;
        try {
            for (Long typeId : notificationTypeIds) {
                if (typeId.equals(1L)) {
                    sendBatchEmails(recipients, notification);
                } else if (typeId.equals(2L)) {
                    sendBatchSms(recipients, notification);
                } else if (typeId.equals(3L)) {
                } else if (typeId.equals(4L)) {
                    sendBatchEmails(recipients, notification);
                    sendBatchSms(recipients, notification);
                } else {
                    logger.warning("Unknown notification type: " + typeId);
                }
            }
            success = true;
        } catch (Exception e) {
            logger.severe("Error processing notifications: " + e.getMessage());
        } finally {
            if (success) {
                updateRecipientsStatus(recipients, "DELIVERED");
            } else {
                updateRecipientsStatus(recipients, "FAILED");
            }
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
        entityManager.flush();
    }

    private void sendBatchEmails(List<NotificationRecipient> recipients, Notification notification) {
        try {
            System.out.println("Hey");
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
        System.out.println("in email with attachment");
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

    @Transactional(readOnly = true)
    public Map<String, Object> getNotificationsBySender(Long facultyId, int offset, int limit) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset cannot be a negative number");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than 0");
        }

        Faculty faculty= entityManager.find(Faculty.class,facultyId);
        if(faculty==null)
        {
            throw new IllegalArgumentException("Faculty with id "+ facultyId + " does not exist");
        }

        // Get total count of notifications
        TypedQuery<Long> countQuery = entityManager.createQuery(
                "SELECT COUNT(n) FROM Notification n WHERE n.sender.id = :facultyId AND n.archived = 'N'", Long.class);
        countQuery.setParameter("facultyId", facultyId);
        Long totalItems = countQuery.getSingleResult();

        // Fetch paginated notifications
        TypedQuery<Notification> query = entityManager.createQuery(
                "SELECT n FROM Notification n WHERE n.sender.id = :facultyId AND n.archived = 'N' " +
                        "ORDER BY n.notificationTimestamp DESC", Notification.class);
        query.setParameter("facultyId", facultyId);
        query.setFirstResult(offset * limit);
        query.setMaxResults(limit);
        List<Notification> notifications = query.getResultList();

        // Calculate total pages
        int totalPages = (int) Math.ceil((double) totalItems / limit);
        if (offset >= totalPages && offset != 0) {
            throw new IllegalArgumentException("No more notifications available");
        }

        // Create pagination response
        Map<String, Object> response = new HashMap<>();
        response.put("notifications", notifications);
        response.put("totalItems", totalItems);
        response.put("totalPages", totalPages);
        response.put("currentPage", offset);

        return response;
    }


    @Transactional(readOnly = true)
    public Map<String, Object> getNotificationsForStudent(Long studentId, int offset, int limit) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset cannot be a negative number");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than 0");
        }

        Student student= entityManager.find(Student.class,studentId);
        if(student==null)
        {
            throw new IllegalArgumentException("Student with id " + studentId + " not found ");
        }
        // Get total count of notifications for the student
        TypedQuery<Long> countQuery = entityManager.createQuery(
                "SELECT COUNT(nr) FROM NotificationRecipient nr " +
                        "WHERE nr.recipient.id = :studentId AND nr.notification.archived = 'N'", Long.class);
        countQuery.setParameter("studentId", studentId);
        Long totalItems = countQuery.getSingleResult();

        // Fetch paginated notifications
        TypedQuery<NotificationRecipient> query = entityManager.createQuery(
                "SELECT nr FROM NotificationRecipient nr " +
                        "WHERE nr.recipient.id = :studentId AND nr.notification.archived = 'N' " +
                        "ORDER BY nr.notification.notificationTimestamp DESC", NotificationRecipient.class);
        query.setParameter("studentId", studentId);
        query.setFirstResult(offset * limit);
        query.setMaxResults(limit);
        List<NotificationRecipient> recipients = query.getResultList();

        List<Notification> notifications = recipients.stream()
                .map(NotificationRecipient::getNotification)
                .collect(Collectors.toList());

        // Calculate total pages
        int totalPages = (int) Math.ceil((double) totalItems / limit);
        if (offset >= totalPages && offset != 0) {
            throw new IllegalArgumentException("No more notifications available");
        }

        // Create pagination response
        Map<String, Object> response = new HashMap<>();
        response.put("notifications", notifications);
        response.put("totalItems", totalItems);
        response.put("totalPages", totalPages);
        response.put("currentPage", offset);

        return response;
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
}