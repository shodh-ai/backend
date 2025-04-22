package com.shodhAI.ShodhAI.service;

import com.shodhAI.ShodhAI.Entity.*;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.NotificationService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private ExceptionHandlingService exceptionHandlingService;

    @Mock
    private TypedQuery<NotificationType> notificationTypeQuery;

    @Mock
    private TypedQuery<Long> longTypedQuery;

    @Mock
    private TypedQuery<Notification> notificationTypedQuery;

    @Mock
    private TypedQuery<NotificationRecipient> notificationRecipientTypedQuery;

    @Mock
    private TypedQuery<DeliveryStatus> deliveryStatusTypedQuery;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private NotificationService notificationService;

    private Faculty faculty;
    private Student student;
    private Course course;
    private Notification notification;
    private NotificationType notificationType;
    private DeliveryStatus pendingStatus;
    private DeliveryStatus deliveredStatus;
    private DeliveryStatus failedStatus;
    private List<Long> notificationTypeIds;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(notificationService, "fromEmail", "test@example.com");

        faculty = new Faculty();
        faculty.setId(1L);
        faculty.setFirstName("Test Faculty");
        faculty.setCollegeEmail("faculty@example.com");
        faculty.setStudents(new ArrayList<>());

        student = new Student();
        student.setId(1L);
        student.setFirstName("Test Student");
        student.setCollegeEmail("student@example.com");

        faculty.getStudents().add(student);

        course = new Course();
        course.setCourseId(1L);
        course.setCourseTitle("Test Course");
        course.setFacultyMembers(Collections.singletonList(faculty));

        notificationType = new NotificationType();
        notificationType.setId(1L);
        notificationType.setTypeName("Email");

        NotificationType smsType = new NotificationType();
        smsType.setId(2L);
        smsType.setTypeName("SMS");

        NotificationType inAppType = new NotificationType();
        inAppType.setId(3L);
        inAppType.setTypeName("In-App");

        NotificationType allType = new NotificationType();
        allType.setId(4L);
        allType.setTypeName("All");

        notification = new Notification();
        notification.setId(1L);
        notification.setTitle("Test Notification");
        notification.setMessage("This is a test notification");
        notification.setSender(faculty);
        notification.setNotificationTypes(Collections.singletonList(notificationType));
        notification.setNotificationTimestamp(LocalDateTime.now());
        notification.setIsSent(false);
        notification.setArchived('N');
        notification.setRecipients(new ArrayList<>());

        pendingStatus = new DeliveryStatus();
        pendingStatus.setId(1L);
        pendingStatus.setCode("PENDING");
        pendingStatus.setDescription("Pending delivery");

        deliveredStatus = new DeliveryStatus();
        deliveredStatus.setId(2L);
        deliveredStatus.setCode("DELIVERED");
        deliveredStatus.setDescription("Delivered successfully");

        failedStatus = new DeliveryStatus();
        failedStatus.setId(3L);
        failedStatus.setCode("FAILED");
        failedStatus.setDescription("Delivery failed");

        notificationTypeIds = new ArrayList<>();
        notificationTypeIds.add(1L);
    }

    @Test
    @DisplayName("Should create notification successfully")
    void testCreateNotification() {
        // Given
        List<Long> validTypeIds = Collections.singletonList(1L);
        List<Long> existingTypeIds = Collections.singletonList(1L);
        List<NotificationType> types = Collections.singletonList(notificationType);

        when(entityManager.createQuery("SELECT nt.id FROM NotificationType nt", Long.class))
                .thenReturn(longTypedQuery);
        when(longTypedQuery.getResultList()).thenReturn(existingTypeIds);
        when(entityManager.createQuery("SELECT nt FROM NotificationType nt WHERE nt.id IN :ids", NotificationType.class))
                .thenReturn(notificationTypeQuery);
        when(notificationTypeQuery.setParameter("ids", validTypeIds)).thenReturn(notificationTypeQuery);
        when(notificationTypeQuery.getResultList()).thenReturn(types);
        when(entityManager.find(Faculty.class, 1L)).thenReturn(faculty);

        // When
        Notification result = notificationService.createNotification("Test Title", "Test Message", 1L, validTypeIds, null);

        // Then
        assertNotNull(result);
        System.out.println(result.getTitle());
        assertEquals("Test Title", result.getTitle());
        assertEquals("Test Message", result.getMessage());
        assertEquals(faculty, result.getSender());
        assertEquals(1, result.getNotificationTypes().size());
        assertEquals(notificationType, result.getNotificationTypes().get(0));

        verify(entityManager).persist(any(Notification.class));
        verify(entityManager).flush();
        verify(entityManager).refresh(any(Notification.class));
    }

    @Test
    @DisplayName("Should throw exception when message is empty")
    void testCreateNotificationWithEmptyMessage() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> notificationService.createNotification("Test Title", "", 1L, notificationTypeIds, null));
        assertEquals("Message cannot be null or empty ", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when notification type ids are empty")
    void testCreateNotificationWithEmptyTypeIds() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> notificationService.createNotification("Test Title", "Test Message", 1L, Collections.emptyList(), null));
        assertEquals("Notification type ids cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when notification type id is invalid")
    void testCreateNotificationWithInvalidTypeId() {
        // Given
        List<Long> existingTypeIds = Collections.singletonList(1L);
        List<Long> invalidTypeIds = Collections.singletonList(999L);

        when(entityManager.createQuery("SELECT nt.id FROM NotificationType nt", Long.class))
                .thenReturn(longTypedQuery);
        when(longTypedQuery.getResultList()).thenReturn(existingTypeIds);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> notificationService.createNotification("Test Title", "Test Message", 1L, invalidTypeIds, null));
        assertEquals("Invalid Notification Type ID: 999", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when course not found")
    void testSendNotificationToCourseNotFound() {
        // Given
        when(entityManager.find(Course.class, 999L)).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> notificationService.sendNotificationToCourse(999L, notification));
        assertEquals("Course not found with ID: 999", exception.getMessage());
    }

    @Test
    @DisplayName("Should get notification with recipients")
    void testGetNotificationWithRecipients() {
        // Given
        when(entityManager.createQuery(
                "SELECT n FROM Notification n LEFT JOIN FETCH n.recipients WHERE n.id = :id",
                Notification.class)).thenReturn(notificationTypedQuery);
        when(notificationTypedQuery.setParameter("id", 1L)).thenReturn(notificationTypedQuery);
        when(notificationTypedQuery.getSingleResult()).thenReturn(notification);

        // When
        Notification result = notificationService.getNotificationWithRecipients(1L);

        // Then
        assertNotNull(result);
        assertEquals(notification, result);
        verify(entityManager).createQuery(anyString(), eq(Notification.class));
        verify(notificationTypedQuery).setParameter("id", 1L);
        verify(notificationTypedQuery).getSingleResult();
    }

    @Test
    @DisplayName("Should process notifications successfully")
    void testProcessNotifications() throws MessagingException {
        // Given
        List<NotificationRecipient> recipients = new ArrayList<>();
        NotificationRecipient recipient = new NotificationRecipient();
        recipient.setNotification(notification);
        recipient.setRecipient(student);
        recipient.setReadStatus(false);
        recipient.setDeliveryStatus(pendingStatus);
        recipients.add(recipient);

        when(entityManager.createQuery("SELECT ds FROM DeliveryStatus ds WHERE ds.code = :code", DeliveryStatus.class))
                .thenReturn(deliveryStatusTypedQuery);
        when(deliveryStatusTypedQuery.setParameter("code", "DELIVERED")).thenReturn(deliveryStatusTypedQuery);
        when(deliveryStatusTypedQuery.getSingleResult()).thenReturn(deliveredStatus);
        when(entityManager.merge(any(Notification.class))).thenReturn(notification);
        when(entityManager.merge(any(NotificationRecipient.class))).thenReturn(recipient);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        notificationService.processNotifications(notification, recipients);

        // Then
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
        verify(entityManager, atLeastOnce()).merge(any(NotificationRecipient.class));

        // Verify notification was updated correctly
        assertTrue(notification.getIsSent());
    }

    @Test
    @DisplayName("Should send email with attachments")
    void testSendEmailWithAttachments() throws MessagingException {
        // Given
        List<String> recipients = Collections.singletonList("recipient@example.com");
        List<File> attachments = new ArrayList<>();
        File attachment = mock(File.class);
        when(attachment.exists()).thenReturn(true);
        when(attachment.getName()).thenReturn("test.pdf");
        attachments.add(attachment);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        notificationService.sendEmailWithAttachments(recipients, "Test Subject", "Test Content", attachments);

        // Then
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Should throw exception when recipients list is empty")
    void testSendEmailWithEmptyRecipients() {
        // When & Then
        MessagingException exception = assertThrows(MessagingException.class,
                () -> notificationService.sendEmailWithAttachments(Collections.emptyList(), "Test Subject", "Test Content", null));
        assertEquals("Recipients list cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should get notifications by sender")
    void testGetNotificationsBySender() {
        // Given
        List<Notification> notifications = Collections.singletonList(notification);
        when(entityManager.find(Faculty.class, 1L)).thenReturn(faculty);
        when(entityManager.createQuery(
                "SELECT COUNT(n) FROM Notification n WHERE n.sender.id = :facultyId AND n.archived = 'N'", Long.class))
                .thenReturn(longTypedQuery);
        when(longTypedQuery.setParameter("facultyId", 1L)).thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(1L);

        when(entityManager.createQuery(
                "SELECT n FROM Notification n WHERE n.sender.id = :facultyId AND n.archived = 'N' " +
                        "ORDER BY n.notificationTimestamp DESC", Notification.class))
                .thenReturn(notificationTypedQuery);
        when(notificationTypedQuery.setParameter("facultyId", 1L)).thenReturn(notificationTypedQuery);
        when(notificationTypedQuery.setFirstResult(0)).thenReturn(notificationTypedQuery);
        when(notificationTypedQuery.setMaxResults(10)).thenReturn(notificationTypedQuery);
        when(notificationTypedQuery.getResultList()).thenReturn(notifications);

        // When
        Map<String, Object> result = notificationService.getNotificationsBySender(1L, 0, 10);

        // Then
        assertNotNull(result);
        assertEquals(notifications, result.get("notifications"));
        assertEquals(1L, result.get("totalItems"));
        assertEquals(1, result.get("totalPages"));
        assertEquals(0, result.get("currentPage"));
    }

    @Test
    @DisplayName("Should throw exception when faculty not found")
    void testGetNotificationsBySenderFacultyNotFound() {
        // Given
        when(entityManager.find(Faculty.class, 999L)).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> notificationService.getNotificationsBySender(999L, 0, 10));
        assertEquals("Faculty with id 999 does not exist", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when offset is negative")
    void testGetNotificationsBySenderWithNegativeOffset() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> notificationService.getNotificationsBySender(1L, -1, 10));
        assertEquals("Offset cannot be a negative number", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when limit is zero or negative")
    void testGetNotificationsBySenderWithInvalidLimit() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> notificationService.getNotificationsBySender(1L, 0, 0));
        assertEquals("Limit must be greater than 0", exception.getMessage());
    }

    @Test
    @DisplayName("Should get notifications for student")
    void testGetNotificationsForStudent() {
        // Given
        NotificationRecipient recipient = new NotificationRecipient();
        recipient.setNotification(notification);
        recipient.setRecipient(student);
        List<NotificationRecipient> recipients = Collections.singletonList(recipient);

        when(entityManager.find(Student.class, 1L)).thenReturn(student);
        when(entityManager.createQuery(
                "SELECT COUNT(nr) FROM NotificationRecipient nr " +
                        "WHERE nr.recipient.id = :studentId AND nr.notification.archived = 'N'", Long.class))
                .thenReturn(longTypedQuery);
        when(longTypedQuery.setParameter("studentId", 1L)).thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(1L);

        when(entityManager.createQuery(
                "SELECT nr FROM NotificationRecipient nr " +
                        "WHERE nr.recipient.id = :studentId AND nr.notification.archived = 'N' " +
                        "ORDER BY nr.notification.notificationTimestamp DESC", NotificationRecipient.class))
                .thenReturn(notificationRecipientTypedQuery);
        when(notificationRecipientTypedQuery.setParameter("studentId", 1L)).thenReturn(notificationRecipientTypedQuery);
        when(notificationRecipientTypedQuery.setFirstResult(0)).thenReturn(notificationRecipientTypedQuery);
        when(notificationRecipientTypedQuery.setMaxResults(10)).thenReturn(notificationRecipientTypedQuery);
        when(notificationRecipientTypedQuery.getResultList()).thenReturn(recipients);

        // When
        Map<String, Object> result = notificationService.getNotificationsForStudent(1L, 0, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, ((List<Notification>) result.get("notifications")).size());
        assertEquals(notification, ((List<Notification>) result.get("notifications")).get(0));
        assertEquals(1L, result.get("totalItems"));
        assertEquals(1, result.get("totalPages"));
        assertEquals(0, result.get("currentPage"));
    }

    @Test
    @DisplayName("Should throw exception when student not found")
    void testGetNotificationsForStudentNotFound() {
        // Given
        when(entityManager.find(Student.class, 999L)).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> notificationService.getNotificationsForStudent(999L, 0, 10));
        assertEquals("Student with id 999 not found ", exception.getMessage());
    }

    @Test
    @DisplayName("Should mark notification as read")
    void testMarkNotificationAsRead() {
        // Given
        NotificationRecipient recipient = new NotificationRecipient();
        recipient.setNotification(notification);
        recipient.setRecipient(student);
        recipient.setReadStatus(false);
        List<NotificationRecipient> recipients = Collections.singletonList(recipient);

        when(entityManager.createQuery(
                "SELECT nr FROM NotificationRecipient nr " +
                        "WHERE nr.notification.id = :notificationId AND nr.recipient.id = :studentId",
                NotificationRecipient.class))
                .thenReturn(notificationRecipientTypedQuery);
        when(notificationRecipientTypedQuery.setParameter("notificationId", 1L)).thenReturn(notificationRecipientTypedQuery);
        when(notificationRecipientTypedQuery.setParameter("studentId", 1L)).thenReturn(notificationRecipientTypedQuery);
        when(notificationRecipientTypedQuery.getResultList()).thenReturn(recipients);
        when(entityManager.merge(any(NotificationRecipient.class))).thenReturn(recipient);

        // When
        notificationService.markNotificationAsRead(1L, 1L);

        // Then
        ArgumentCaptor<NotificationRecipient> captor = ArgumentCaptor.forClass(NotificationRecipient.class);
        verify(entityManager).merge(captor.capture());
        NotificationRecipient captured = captor.getValue();
        assertTrue(captured.getReadStatus());
        assertNotNull(captured.getReadDate());
    }

    @Test
    @DisplayName("Should do nothing when notification recipient not found")
    void testMarkNotificationAsReadNotFound() {
        // Given
        when(entityManager.createQuery(
                "SELECT nr FROM NotificationRecipient nr " +
                        "WHERE nr.notification.id = :notificationId AND nr.recipient.id = :studentId",
                NotificationRecipient.class))
                .thenReturn(notificationRecipientTypedQuery);
        when(notificationRecipientTypedQuery.setParameter("notificationId", 1L)).thenReturn(notificationRecipientTypedQuery);
        when(notificationRecipientTypedQuery.setParameter("studentId", 1L)).thenReturn(notificationRecipientTypedQuery);
        when(notificationRecipientTypedQuery.getResultList()).thenReturn(Collections.emptyList());

        // When
        notificationService.markNotificationAsRead(1L, 1L);

        // Then
        verify(entityManager, never()).merge(any(NotificationRecipient.class));
    }
}