-- DATE: 21-March-2025 13:00:00
-- Student table changes
ALTER TABLE student ALTER COLUMN college_email DROP NOT NULL;
ALTER TABLE student ALTER COLUMN gender_id DROP NOT NULL;
ALTER TABLE student ALTER COLUMN academic_degree_id DROP NOT NULL;
-- Faculty table changes
ALTER TABLE faculty ALTER COLUMN college_email DROP NOT NULL;
ALTER TABLE faculty ALTER COLUMN gender_id DROP NOT NULL;

-- DATE: 31-March-2025 14:30:00
ALTER TABLE user_subtopic_progress ALTER COLUMN user_topic_progress_id DROP NOT NULL;
ALTER TABLE user_topic_progress ALTER COLUMN user_module_progress_id DROP NOT NULL;
ALTER TABLE user_module_progress ALTER COLUMN user_course_progress_id DROP NOT NULL;
ALTER TABLE user_course_progress ALTER COLUMN user_semester_progress_id DROP NOT NULL;