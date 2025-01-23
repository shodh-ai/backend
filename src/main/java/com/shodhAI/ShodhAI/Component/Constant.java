package com.shodhAI.ShodhAI.Component;

public class Constant {

    public static String GET_ALL_ROLES = "SELECT r FROM Role r";
    public static String GET_ROLE_BY_ID = "SELECT r FROM Role r WHERE r.roleId = : roleId";

    public static String GET_ALL_GENDERS = "SELECT g FROM Gender g AND g.archived = 'N'";
    public static String GET_GENDER_BY_ID = "SELECT g FROM Gender g WHERE g.genderId = :genderId";

    public static String GET_ALL_ACADEMIC_DEGREES = "SELECT a FROM AcademicDegree a WHERE a.archived = 'N'";
    public static String GET_ACADEMIC_DEGREE_BY_ID = "SELECT a FROM AcademicDegree a WHERE a.degreeId = :degreeId";

    public static String GET_ALL_STUDENT = "SELECT s FROM Student s WHERE s.archived = 'N'";
    public static String GET_STUDENT_BY_ID = "SELECT s FROM Student s WHERE s.id = :studentId";
    public static String GET_STUDENT_LEADERBOARD = "SELECT s FROM Student s WHERE s.archived = 'N' ORDER BY s.marksObtained DESC";
}
