package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Entity.AcademicDegree;
import com.shodhAI.ShodhAI.Entity.ContentType;
import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.DeliveryStatus;
import com.shodhAI.ShodhAI.Entity.DoubtLevel;
import com.shodhAI.ShodhAI.Entity.FileType;
import com.shodhAI.ShodhAI.Entity.Gender;
import com.shodhAI.ShodhAI.Entity.NotificationType;
import com.shodhAI.ShodhAI.Entity.PriorityLevel;
import com.shodhAI.ShodhAI.Entity.QuestionType;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.Module;
import com.shodhAI.ShodhAI.Entity.Semester;
import com.shodhAI.ShodhAI.Entity.TopicType;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CommandLineService implements CommandLineRunner {

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if (entityManager.createQuery("SELECT count(g) FROM Gender g", Long.class).getSingleResult() == 0) {
            entityManager.merge(new Gender(1L, 'M', "Male"));
            entityManager.merge(new Gender(2L, 'F', "Female"));
            entityManager.merge(new Gender(3L, 'O', "Others"));
        }

        if (entityManager.createQuery("SELECT count(r) FROM Role r", Long.class).getSingleResult() == 0) {
            Date currentDate = new Date();

            entityManager.merge(new Role(1L, currentDate, "SUPER_ADMIN", currentDate));
            entityManager.merge(new Role(2L, currentDate, "ADMIN", currentDate));
            entityManager.merge(new Role(3L, currentDate, "FACULTY", currentDate));
            entityManager.merge(new Role(4L, currentDate, "STUDENT", currentDate));
        }

        if (entityManager.createQuery("SELECT COUNT(f) FROM FileType f", Long.class).getSingleResult() == 0) {
            entityManager.merge(new FileType(1L, "PNG"));
            entityManager.merge(new FileType(2L, "JPG"));
            entityManager.merge(new FileType(3L, "PDF"));
            entityManager.merge(new FileType(4L, "JPEG"));
            entityManager.merge(new FileType(5L, "MP4"));
            entityManager.merge(new FileType(6L, "TXT"));
        }

        if (entityManager.createQuery("SELECT COUNT(t) FROM TopicType t", Long.class).getSingleResult() == 0) {

            Date currentDate = new Date();

            entityManager.merge(new TopicType(1L, "TEACHING", 'N', currentDate, currentDate));
            entityManager.merge(new TopicType(2L, "ASSIGNMENT", 'N', currentDate, currentDate));
            entityManager.merge(new TopicType(3L, "ASSESSMENT", 'N', currentDate, currentDate));
            entityManager.merge(new TopicType(4L, "SIMULATION", 'N', currentDate, currentDate));
        }

        if (entityManager.createQuery("SELECT COUNT(c) FROM ContentType c", Long.class).getSingleResult() == 0) {

            Date currentDate = new Date();

            entityManager.merge(new ContentType(1L, "TEACHING", 'N', currentDate, currentDate));
            entityManager.merge(new ContentType(2L, "PRACTICE_QUESTION", 'N', currentDate, currentDate));
            entityManager.merge(new ContentType(3L, "ASSIGNMENT_QUESTION", 'N', currentDate, currentDate));
            entityManager.merge(new ContentType(4L, "SIMULATION", 'N', currentDate, currentDate));
        }

        if (entityManager.createQuery("SELECT COUNT(c) FROM QuestionType c", Long.class).getSingleResult() == 0) {

            Date currentDate = new Date();

            entityManager.merge(new QuestionType(1L, "PRACTICE_QUESTION", 'N', currentDate, currentDate));
            entityManager.merge(new QuestionType(2L, "EXAMPLE_QUESTION", 'N', currentDate, currentDate));
            entityManager.merge(new QuestionType(3L, "TESTING_QUESTION", 'N', currentDate, currentDate));
            entityManager.merge(new QuestionType(4L, "QUIZ", 'N', currentDate, currentDate));
        }

        if (entityManager.createQuery("SELECT COUNT(d) FROM DoubtLevel d", Long.class).getSingleResult() == 0) {

            Date currentDate = new Date();

            entityManager.merge(new DoubtLevel(1L, "BASIC", 'N', currentDate, currentDate));
            entityManager.merge(new ContentType(2L, "MEDIATE", 'N', currentDate, currentDate));
            entityManager.merge(new ContentType(3L, "ADVANCED", 'N', currentDate, currentDate));
        }

        if (entityManager.createQuery("SELECT COUNT(p) FROM PriorityLevel p", Long.class).getSingleResult() == 0) {

            Date currentDate = new Date();

            entityManager.merge(new PriorityLevel(1L, "HIGH", 'N', currentDate, currentDate));
            entityManager.merge(new PriorityLevel(2L, "MEDIUM", 'N', currentDate, currentDate));
            entityManager.merge(new PriorityLevel(3L, "LOW+", 'N', currentDate, currentDate));
        }

        if (entityManager.createQuery("SELECT count(nt) FROM NotificationType nt", Long.class).getSingleResult() == 0) {
            entityManager.merge(new NotificationType(1L, "EMAIL", "Email Notification", 'N',null));
            entityManager.merge(new NotificationType(2L, "SMS", "SMS Notification", 'N',null));
            entityManager.merge(new NotificationType(3L, "APP", "In-App Notification",'N', null));
            entityManager.merge(new NotificationType(4L, "ALL", "All Notification Channels", 'N',null));
        }

        if (entityManager.createQuery("SELECT count(ds) FROM DeliveryStatus ds", Long.class).getSingleResult() == 0) {
            entityManager.merge(new DeliveryStatus(1L, "PENDING", "Notification is pending delivery" ,'N',null));
            entityManager.merge(new DeliveryStatus(2L, "DELIVERED", "Notification has been delivered",'N', null));
            entityManager.merge(new DeliveryStatus(3L, "FAILED", "Notification delivery failed",'N', null));
        }

        if (entityManager.createQuery("SELECT count(ad) FROM AcademicDegree ad", Long.class).getSingleResult() == 0) {
            entityManager.merge(new AcademicDegree(1L, "Master of Business Administration", "MBA", "Gyan Vihar University", 'N', new Date()));
            entityManager.merge(new AcademicDegree(2L, "Bachelor of Science", "BSc", "XYZ University", 'N', new Date()));
            entityManager.merge(new AcademicDegree(3L, "Master of Science", "MSc", "ABC University", 'N', new Date()));
            entityManager.merge(new AcademicDegree(4L, "Doctor of Philosophy", "PhD", "LMN Institute", 'N', new Date()));
        }

        if(entityManager.createQuery("SELECT count(s) FROM Semester s",Long.class).getSingleResult()==0)
        {
          entityManager.merge(new Semester(1L, "Semester 1", new Date(), new Date(), 'N'));
          entityManager.merge(new Semester(2L, "Semester 2", new Date(), new Date(), 'N'));
          entityManager.merge(new Semester(3L, "Semester 3", new Date(), new Date(), 'N'));
          entityManager.merge(new Semester(4L, "Semester 4", new Date(), new Date(), 'N'));
        }

        if (entityManager.createQuery("SELECT count(c) FROM Course c", Long.class).getSingleResult() == 0) {
            // MBA Courses
            entityManager.merge(new Course(1L, "Business Fundamentals", "Introduction to Business", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(2L, "Financial Management", "Corporate Finance & Accounting", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(3L, "Marketing Strategies", "Marketing & Brand Management", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(4L, "Organizational Behavior", "Leadership & Team Management", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(5L, "Operations Management", "Logistics & Supply Chain", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(6L, "Strategic Management", "Corporate Strategy & Business Policy", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(7L, "Human Resource Management", "Talent Acquisition & Performance Management", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(8L, "International Business", "Global Business & Trade", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(9L, "Entrepreneurship", "Business Startups & Innovation", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(10L, "Digital Marketing", "SEO, SEM & Social Media Marketing", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(11L, "Business Analytics", "Data Analysis for Business Decisions", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(12L, "E-Commerce Management", "Online Business Strategies", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(13L, "Leadership & Ethics", "Corporate Governance & Ethical Decision-Making", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(14L, "Supply Chain Management", "Inventory & Vendor Management", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(15L, "Corporate Law", "Legal Aspects of Business", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(16L, "Negotiation Skills", "Conflict Resolution & Bargaining", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(17L, "Project Management", "Planning, Execution & Risk Management", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(18L, "Customer Relationship Management", "Client Handling & Service Quality", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(19L, "Business Communication", "Effective Corporate Communication", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(20L, "Financial Risk Management", "Managing Risks in Investments & Business", 'N', new Date(), "6 months", new Date(), new Date()));

            // Other Courses (BSc, MSc, PhD)
            entityManager.merge(new Course(21L, "Physics 101", "Classical & Quantum Mechanics", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(22L, "Chemistry Basics", "Organic & Inorganic Chemistry", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(23L, "Advanced Data Science", "Machine Learning & AI", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(24L, "Doctoral Research Methods", "Quantitative & Qualitative Research", 'N', new Date(), "6 months", new Date(), new Date()));
        }

        // Check if modules already exist
        if (entityManager.createQuery("SELECT count(m) FROM Module m", Long.class).getSingleResult() == 0) {
            Date currentDate = new Date();

            // Get courses references to set in modules
            Map<Long, Course> courses = new HashMap<>();
            List<Course> courseList = entityManager.createQuery("SELECT c FROM Course c", Course.class).getResultList();
            for (Course course : courseList) {
                courses.put(course.getCourseId(), course);
            }

            // Business Fundamentals Modules
            Module module1 = new Module(1L, "Introduction to Management", "Basic concepts of management and organizational structures", 'N', currentDate, "4 weeks");
            module1.setCourse(courses.get(1L));
            entityManager.merge(module1);

            Module module2 = new Module(2L, "Principles of Economics", "Microeconomics and macroeconomics for business decisions", 'N', currentDate, "4 weeks");
            module2.setCourse(courses.get(1L));
            entityManager.merge(module2);

            Module module3 = new Module(3L, "Business Ethics", "Ethical considerations in business decisions and corporate social responsibility", 'N', currentDate, "4 weeks");
            module3.setCourse(courses.get(1L));
            entityManager.merge(module3);

            // Financial Management Modules
            Module module4 = new Module(4L, "Corporate Finance", "Financial decision-making in corporate environments", 'N', currentDate, "4 weeks");
            module4.setCourse(courses.get(2L));
            entityManager.merge(module4);

            Module module5 = new Module(5L, "Financial Accounting", "Principles of accounting and financial statement analysis", 'N', currentDate, "4 weeks");
            module5.setCourse(courses.get(2L));
            entityManager.merge(module5);

            Module module6 = new Module(6L, "Investment Analysis", "Evaluation of investment opportunities and portfolio management", 'N', currentDate, "4 weeks");
            module6.setCourse(courses.get(2L));
            entityManager.merge(module6);

            // Marketing Strategies Modules
            Module module7 = new Module(7L, "Consumer Behavior", "Understanding consumer psychology and purchase decisions", 'N', currentDate, "4 weeks");
            module7.setCourse(courses.get(3L));
            entityManager.merge(module7);

            Module module8 = new Module(8L, "Brand Management", "Building and maintaining strong brands", 'N', currentDate, "4 weeks");
            module8.setCourse(courses.get(3L));
            entityManager.merge(module8);

            Module module9 = new Module(9L, "Market Research", "Methods and techniques for gathering market intelligence", 'N', currentDate, "4 weeks");
            module9.setCourse(courses.get(3L));
            entityManager.merge(module9);

            // Organizational Behavior Modules
            Module module10 = new Module(10L, "Leadership Styles", "Different approaches to leadership and their effectiveness", 'N', currentDate, "4 weeks");
            module10.setCourse(courses.get(4L));
            entityManager.merge(module10);

            Module module11 = new Module(11L, "Team Dynamics", "Building and managing effective teams", 'N', currentDate, "4 weeks");
            module11.setCourse(courses.get(4L));
            entityManager.merge(module11);

            Module module12 = new Module(12L, "Conflict Resolution", "Strategies for managing workplace conflicts", 'N', currentDate, "4 weeks");
            module12.setCourse(courses.get(4L));
            entityManager.merge(module12);

            // Operations Management Modules
            Module module13 = new Module(13L, "Supply Chain Optimization", "Streamlining supply chain processes for efficiency", 'N', currentDate, "4 weeks");
            module13.setCourse(courses.get(5L));
            entityManager.merge(module13);

            Module module14 = new Module(14L, "Quality Management", "Systems and approaches for ensuring product and service quality", 'N', currentDate, "4 weeks");
            module14.setCourse(courses.get(5L));
            entityManager.merge(module14);

            Module module15 = new Module(15L, "Process Improvement", "Continuous improvement methodologies like Six Sigma and Lean", 'N', currentDate, "4 weeks");
            module15.setCourse(courses.get(5L));
            entityManager.merge(module15);

            // Strategic Management Modules
            Module module16 = new Module(16L, "Strategic Planning", "Developing and implementing organizational strategies", 'N', currentDate, "4 weeks");
            module16.setCourse(courses.get(6L));
            entityManager.merge(module16);

            Module module17 = new Module(17L, "Competitive Analysis", "Evaluating industry competition and market positioning", 'N', currentDate, "4 weeks");
            module17.setCourse(courses.get(6L));
            entityManager.merge(module17);

            Module module18 = new Module(18L, "Business Policy", "Formulating policies that align with organizational goals", 'N', currentDate, "4 weeks");
            module18.setCourse(courses.get(6L));
            entityManager.merge(module18);

            // Human Resource Management Modules
            Module module19 = new Module(19L, "Talent Acquisition", "Recruiting and selecting the right candidates", 'N', currentDate, "4 weeks");
            module19.setCourse(courses.get(7L));
            entityManager.merge(module19);

            Module module20 = new Module(20L, "Performance Management", "Evaluating and improving employee performance", 'N', currentDate, "4 weeks");
            module20.setCourse(courses.get(7L));
            entityManager.merge(module20);

            Module module21 = new Module(21L, "Training & Development", "Employee skill development and career progression", 'N', currentDate, "4 weeks");
            module21.setCourse(courses.get(7L));
            entityManager.merge(module21);

            // International Business Modules
            Module module22 = new Module(22L, "Global Markets", "Understanding international trade and market entry strategies", 'N', currentDate, "4 weeks");
            module22.setCourse(courses.get(8L));
            entityManager.merge(module22);

            Module module23 = new Module(23L, "Cross-Cultural Management", "Working effectively across different cultures", 'N', currentDate, "4 weeks");
            module23.setCourse(courses.get(8L));
            entityManager.merge(module23);

            Module module24 = new Module(24L, "International Trade Law", "Legal frameworks governing international business", 'N', currentDate, "4 weeks");
            module24.setCourse(courses.get(8L));
            entityManager.merge(module24);

            // Entrepreneurship Modules
            Module module25 = new Module(25L, "Business Model Design", "Creating viable business models for startups", 'N', currentDate, "4 weeks");
            module25.setCourse(courses.get(9L));
            entityManager.merge(module25);

            Module module26 = new Module(26L, "Startup Funding", "Securing financing for new ventures", 'N', currentDate, "4 weeks");
            module26.setCourse(courses.get(9L));
            entityManager.merge(module26);

            Module module27 = new Module(27L, "Innovation Management", "Fostering and implementing innovative ideas", 'N', currentDate, "4 weeks");
            module27.setCourse(courses.get(9L));
            entityManager.merge(module27);

            // Digital Marketing Modules
            Module module28 = new Module(28L, "Search Engine Optimization", "Optimizing online content for search engines", 'N', currentDate, "4 weeks");
            module28.setCourse(courses.get(10L));
            entityManager.merge(module28);

            Module module29 = new Module(29L, "Social Media Marketing", "Creating effective social media campaigns", 'N', currentDate, "4 weeks");
            module29.setCourse(courses.get(10L));
            entityManager.merge(module29);

            Module module30 = new Module(30L, "Content Marketing", "Developing valuable content to attract and engage customers", 'N', currentDate, "4 weeks");
            module30.setCourse(courses.get(10L));
            entityManager.merge(module30);

            // Add modules for the remaining courses
            // Business Analytics Modules
            Module module31 = new Module(31L, "Data Analysis Fundamentals", "Basic concepts and techniques in data analysis", 'N', currentDate, "4 weeks");
            module31.setCourse(courses.get(11L));
            entityManager.merge(module31);

            Module module32 = new Module(32L, "Predictive Analytics", "Using data to forecast future trends and behaviors", 'N', currentDate, "4 weeks");
            module32.setCourse(courses.get(11L));
            entityManager.merge(module32);

            Module module33 = new Module(33L, "Business Intelligence Tools", "Utilizing BI software for data visualization and analysis", 'N', currentDate, "4 weeks");
            module33.setCourse(courses.get(11L));
            entityManager.merge(module33);

            // E-Commerce Management Modules
            Module module34 = new Module(34L, "Online Retail Strategies", "Developing successful e-commerce business models", 'N', currentDate, "4 weeks");
            module34.setCourse(courses.get(12L));
            entityManager.merge(module34);

            Module module35 = new Module(35L, "E-Commerce Platforms", "Evaluating and implementing digital marketplace solutions", 'N', currentDate, "4 weeks");
            module35.setCourse(courses.get(12L));
            entityManager.merge(module35);

            Module module36 = new Module(36L, "Customer Experience Design", "Creating user-friendly online shopping experiences", 'N', currentDate, "4 weeks");
            module36.setCourse(courses.get(12L));
            entityManager.merge(module36);

            // Leadership & Ethics Modules
            Module module37 = new Module(37L, "Ethical Leadership", "Leading with integrity and ethical decision-making", 'N', currentDate, "4 weeks");
            module37.setCourse(courses.get(13L));
            entityManager.merge(module37);

            Module module38 = new Module(38L, "Corporate Governance", "Principles and practices of good governance in organizations", 'N', currentDate, "4 weeks");
            module38.setCourse(courses.get(13L));
            entityManager.merge(module38);

            Module module39 = new Module(39L, "Ethical Dilemmas in Business", "Case studies and frameworks for resolving ethical challenges", 'N', currentDate, "4 weeks");
            module39.setCourse(courses.get(13L));
            entityManager.merge(module39);

            // Supply Chain Management Modules
            Module module40 = new Module(40L, "Inventory Management", "Techniques for optimal inventory control", 'N', currentDate, "4 weeks");
            module40.setCourse(courses.get(14L));
            entityManager.merge(module40);

            Module module41 = new Module(41L, "Vendor Relations", "Building and managing supplier relationships", 'N', currentDate, "4 weeks");
            module41.setCourse(courses.get(14L));
            entityManager.merge(module41);

            Module module42 = new Module(42L, "Logistics Operations", "Planning and executing efficient product distribution", 'N', currentDate, "4 weeks");
            module42.setCourse(courses.get(14L));
            entityManager.merge(module42);

            // Corporate Law Modules
            Module module43 = new Module(43L, "Contract Law", "Creating and managing business contracts", 'N', currentDate, "4 weeks");
            module43.setCourse(courses.get(15L));
            entityManager.merge(module43);

            Module module44 = new Module(44L, "Intellectual Property", "Protecting creative assets and innovations", 'N', currentDate, "4 weeks");
            module44.setCourse(courses.get(15L));
            entityManager.merge(module44);

            Module module45 = new Module(45L, "Business Regulations", "Navigating government regulations and compliance", 'N', currentDate, "4 weeks");
            module45.setCourse(courses.get(15L));
            entityManager.merge(module45);

            // Negotiation Skills Modules
            Module module46 = new Module(46L, "Negotiation Strategies", "Techniques for achieving win-win outcomes", 'N', currentDate, "4 weeks");
            module46.setCourse(courses.get(16L));
            entityManager.merge(module46);

            Module module47 = new Module(47L, "Conflict Resolution", "Managing and resolving business disputes", 'N', currentDate, "4 weeks");
            module47.setCourse(courses.get(16L));
            entityManager.merge(module47);

            Module module48 = new Module(48L, "Persuasion Techniques", "Influencing others effectively and ethically", 'N', currentDate, "4 weeks");
            module48.setCourse(courses.get(16L));
            entityManager.merge(module48);

            // Project Management Modules
            Module module49 = new Module(49L, "Project Planning", "Defining scope, schedule, and resources for projects", 'N', currentDate, "4 weeks");
            module49.setCourse(courses.get(17L));
            entityManager.merge(module49);

            Module module50 = new Module(50L, "Risk Management", "Identifying and mitigating project risks", 'N', currentDate, "4 weeks");
            module50.setCourse(courses.get(17L));
            entityManager.merge(module50);

            Module module51 = new Module(51L, "Agile Project Management", "Implementing flexible project methodologies", 'N', currentDate, "4 weeks");
            module51.setCourse(courses.get(17L));
            entityManager.merge(module51);

            // Customer Relationship Management Modules
            Module module52 = new Module(52L, "Customer Service Excellence", "Delivering exceptional customer experiences", 'N', currentDate, "4 weeks");
            module52.setCourse(courses.get(18L));
            entityManager.merge(module52);

            Module module53 = new Module(53L, "CRM Systems", "Implementing and leveraging CRM technologies", 'N', currentDate, "4 weeks");
            module53.setCourse(courses.get(18L));
            entityManager.merge(module53);

            Module module54 = new Module(54L, "Customer Retention Strategies", "Building long-term customer loyalty", 'N', currentDate, "4 weeks");
            module54.setCourse(courses.get(18L));
            entityManager.merge(module54);

            // Business Communication Modules
            Module module55 = new Module(55L, "Professional Writing", "Creating clear and effective business documents", 'N', currentDate, "4 weeks");
            module55.setCourse(courses.get(19L));
            entityManager.merge(module55);

            Module module56 = new Module(56L, "Presentation Skills", "Delivering impactful business presentations", 'N', currentDate, "4 weeks");
            module56.setCourse(courses.get(19L));
            entityManager.merge(module56);

            Module module57 = new Module(57L, "Interpersonal Communication", "Building rapport and effective workplace relationships", 'N', currentDate, "4 weeks");
            module57.setCourse(courses.get(19L));
            entityManager.merge(module57);

            // Financial Risk Management Modules
            Module module58 = new Module(58L, "Risk Assessment", "Identifying and evaluating financial risks", 'N', currentDate, "4 weeks");
            module58.setCourse(courses.get(20L));
            entityManager.merge(module58);

            Module module59 = new Module(59L, "Hedging Strategies", "Techniques for mitigating financial risk", 'N', currentDate, "4 weeks");
            module59.setCourse(courses.get(20L));
            entityManager.merge(module59);

            Module module60 = new Module(60L, "Portfolio Risk Management", "Managing risk across investment portfolios", 'N', currentDate, "4 weeks");
            module60.setCourse(courses.get(20L));
            entityManager.merge(module60);

            // Physics 101 Modules
            Module module61 = new Module(61L, "Classical Mechanics", "Motion, forces, and energy in physical systems", 'N', currentDate, "4 weeks");
            module61.setCourse(courses.get(21L));
            entityManager.merge(module61);

            Module module62 = new Module(62L, "Quantum Mechanics", "Fundamental principles of quantum physics", 'N', currentDate, "4 weeks");
            module62.setCourse(courses.get(21L));
            entityManager.merge(module62);

            Module module63 = new Module(63L, "Thermodynamics", "Heat, energy, and physical systems", 'N', currentDate, "4 weeks");
            module63.setCourse(courses.get(21L));
            entityManager.merge(module63);

            // Chemistry Basics Modules
            Module module64 = new Module(64L, "Organic Chemistry", "Carbon-based compounds and reactions", 'N', currentDate, "4 weeks");
            module64.setCourse(courses.get(22L));
            entityManager.merge(module64);

            Module module65 = new Module(65L, "Inorganic Chemistry", "Non-carbon-based chemical principles", 'N', currentDate, "4 weeks");
            module65.setCourse(courses.get(22L));
            entityManager.merge(module65);

            Module module66 = new Module(66L, "Analytical Chemistry", "Techniques for analyzing chemical compositions", 'N', currentDate, "4 weeks");
            module66.setCourse(courses.get(22L));
            entityManager.merge(module66);

            // Advanced Data Science Modules
            Module module67 = new Module(67L, "Machine Learning Algorithms", "Supervised and unsupervised learning techniques", 'N', currentDate, "4 weeks");
            module67.setCourse(courses.get(23L));
            entityManager.merge(module67);

            Module module68 = new Module(68L, "Deep Learning", "Neural networks and advanced AI concepts", 'N', currentDate, "4 weeks");
            module68.setCourse(courses.get(23L));
            entityManager.merge(module68);

            Module module69 = new Module(69L, "Big Data Analytics", "Processing and analyzing large-scale datasets", 'N', currentDate, "4 weeks");
            module69.setCourse(courses.get(23L));
            entityManager.merge(module69);

            // Doctoral Research Methods Modules
            Module module70 = new Module(70L, "Quantitative Research", "Statistical methods and data analysis for research", 'N', currentDate, "4 weeks");
            module70.setCourse(courses.get(24L));
            entityManager.merge(module70);

            Module module71 = new Module(71L, "Qualitative Research", "Interview, observation, and textual analysis methods", 'N', currentDate, "4 weeks");
            module71.setCourse(courses.get(24L));
            entityManager.merge(module71);

            Module module72 = new Module(72L, "Research Ethics", "Ethical considerations in academic research", 'N', currentDate, "4 weeks");
            module72.setCourse(courses.get(24L));
            entityManager.merge(module72);
        }


    }
}