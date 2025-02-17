package com.shodhAI.ShodhAI.Component;

public class Constant {

    public static String GET_ALL_ROLES = "SELECT r FROM Role r";
    public static String GET_ROLE_BY_ID = "SELECT r FROM Role r WHERE r.roleId = : roleId";

    public static String GET_ALL_GENDERS = "SELECT g FROM Gender g";
    public static String GET_GENDER_BY_ID = "SELECT g FROM Gender g WHERE g.genderId = :genderId";

    public static String GET_ALL_FILE_TYPES = "SELECT f FROM FileType f WHERE f.archived = 'N'";
    public static String GET_FILE_TYPE_BY_ID = "SELECT f FROM FileType f WHERE f.fileTypeId = :fileTypeId";
    public static String GET_FILE_TYPE_BY_TYPE = "SELECT f FROM FileType f WHERE LOWER(f.fileTypeName) = LOWER(:fileType)";

    public static String GET_ALL_ACADEMIC_DEGREES = "SELECT a FROM AcademicDegree a WHERE a.archived = 'N'";
    public static String GET_ACADEMIC_DEGREE_BY_ID = "SELECT a FROM AcademicDegree a WHERE a.degreeId = :degreeId";

    public static String GET_ALL_STUDENT = "SELECT s FROM Student s WHERE s.archived = 'N'";
    public static String GET_STUDENT_BY_ID = "SELECT s FROM Student s WHERE s.id = :studentId";
    public static String GET_STUDENT_LEADERBOARD = "SELECT s FROM Student s WHERE s.archived = 'N' ORDER BY s.marksObtained DESC";

    public static String GET_MODULE_BY_ID = "SELECT m FROM Module m WHERE m.archived = 'N' AND m.moduleId = :moduleId";

    public static String GET_TOPIC_BY_ID = "SELECT t FROM Topic t WHERE t.archived = 'N' AND t.topicId = :topicId";

    public static String GET_ALL_COURSES = "SELECT c FROM Course c WHERE c.archived = 'N'";
    public static String GET_COURSE_BY_ID = "SELECT c FROM Course c WHERE c.courseId = :courseId";

    public static String GET_CONTENT_BY_TOPIC = "SELECT c FROM Content c WHERE c.topic = :topic";
    public static String GET_CONTENT_BY_ID = "SELECT c FROM Content c WHERE c.contentId = :contentId";

    public static String GET_QUESTION_BY_TOPIC = "SELECT q FROM Question q WHERE q.topic = :topic";
}
