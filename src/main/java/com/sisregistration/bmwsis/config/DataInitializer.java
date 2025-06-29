package com.sisregistration.bmwsis.config;

import com.sisregistration.bmwsis.entity.*;
import com.sisregistration.bmwsis.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Autowired
    private FacultyRepository facultyRepository;
    
    @Autowired
    private SubjectScheduleRepository subjectScheduleRepository;
    
    @Autowired
    private EnrollmentPeriodRepository enrollmentPeriodRepository;
    
    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private SectionRepository sectionRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        try {
            initializeDefaultAccounts();
            initializeAdminAccounts();
            
            // Create sample students
            if (studentRepository.count() == 0) {
            // 1st Year Students
            Student student1 = new Student("2024-0001", passwordEncoder.encode("password123"), "Alice", "Johnson", 
                                         "alice.johnson@email.com", "Bachelor of Science in Information Technology", 1, "IT-1A");
            Student student2 = new Student("2024-0002", passwordEncoder.encode("password123"), "Bob", "Wilson", 
                                         "bob.wilson@email.com", "Bachelor of Science in Information Technology", 1, "IT-1B");
            
            // 2nd Year Students
            Student student3 = new Student("2023-0001", passwordEncoder.encode("password123"), "Carla", "Smith", 
                                         "carla.smith@email.com", "Bachelor of Science in Information Technology", 2, "IT-2A");
            Student student4 = new Student("2023-0002", passwordEncoder.encode("password123"), "David", "Brown", 
                                         "david.brown@email.com", "Bachelor of Science in Information Technology", 2, "IT-2B");
            Student student5 = new Student("2023-0003", passwordEncoder.encode("password123"), "Mike", "Johnson", 
                                         "mike.johnson@email.com", "Bachelor of Science in Information Technology", 2, "IT-2A");
            
            // 3rd Year Students
            Student student6 = new Student("2022-0001", passwordEncoder.encode("password123"), "John", "Doe", 
                                         "john.doe@email.com", "Bachelor of Science in Information Technology", 3, "IT-3A");
            Student student7 = new Student("2022-0002", passwordEncoder.encode("password123"), "Jane", "Davis", 
                                         "jane.davis@email.com", "Bachelor of Science in Information Technology", 3, "IT-3B");
            
            studentRepository.save(student1);
            studentRepository.save(student2);
            studentRepository.save(student3);
            studentRepository.save(student4);
            studentRepository.save(student5);
            studentRepository.save(student6);
            studentRepository.save(student7);
        }

        // Create sample faculty
        if (facultyRepository.count() == 0) {
            Faculty faculty1 = new Faculty("FAC-001", "Dr. Maria", "Garcia", "maria.garcia@bmw.edu", "Computer Science", passwordEncoder.encode("faculty123"));
            Faculty faculty2 = new Faculty("FAC-002", "Prof. Robert", "Chen", "robert.chen@bmw.edu", "Information Technology", passwordEncoder.encode("faculty123"));
            Faculty faculty3 = new Faculty("FAC-003", "Dr. Sarah", "Williams", "sarah.williams@bmw.edu", "Software Engineering", passwordEncoder.encode("faculty123"));
            Faculty faculty4 = new Faculty("FAC-004", "Prof. David", "Brown", "david.brown@bmw.edu", "Database Systems", passwordEncoder.encode("faculty123"));
            Faculty faculty5 = new Faculty("FAC-005", "Dr. Lisa", "Anderson", "lisa.anderson@bmw.edu", "Network Security", passwordEncoder.encode("faculty123"));
            
            facultyRepository.save(faculty1);
            facultyRepository.save(faculty2);
            facultyRepository.save(faculty3);
            facultyRepository.save(faculty4);
            facultyRepository.save(faculty5);
        }

        // Create sample sections
        if (sectionRepository.count() == 0) {
            Faculty faculty1 = facultyRepository.findByFacultyId("FAC-001").orElse(null);
            Faculty faculty2 = facultyRepository.findByFacultyId("FAC-002").orElse(null);
            Faculty faculty3 = facultyRepository.findByFacultyId("FAC-003").orElse(null);
            
            // IT Sections
            Section section1A = new Section("IT-1A", "Information Technology 1A", "IT", 1, "2024-2025", 1, 35);
            section1A.setAdviser(faculty1);
            section1A.setCurrentEnrollment(1); // Alice Johnson
            section1A.setRoomAssignment("Room 101");
            
            Section section1B = new Section("IT-1B", "Information Technology 1B", "IT", 1, "2024-2025", 1, 35);
            section1B.setAdviser(faculty2);
            section1B.setCurrentEnrollment(1); // Bob Wilson
            section1B.setRoomAssignment("Room 102");
            
            Section section2A = new Section("IT-2A", "Information Technology 2A", "IT", 2, "2024-2025", 1, 30);
            section2A.setAdviser(faculty1);
            section2A.setCurrentEnrollment(2); // Carla Smith, Mike Johnson
            section2A.setRoomAssignment("Room 201");
            
            Section section2B = new Section("IT-2B", "Information Technology 2B", "IT", 2, "2024-2025", 1, 30);
            section2B.setAdviser(faculty2);
            section2B.setCurrentEnrollment(1); // David Brown
            section2B.setRoomAssignment("Room 202");
            
            Section section3A = new Section("IT-3A", "Information Technology 3A", "IT", 3, "2024-2025", 1, 25);
            section3A.setAdviser(faculty3);
            section3A.setCurrentEnrollment(1); // John Doe
            section3A.setRoomAssignment("Room 301");
            
            Section section3B = new Section("IT-3B", "Information Technology 3B", "IT", 3, "2024-2025", 1, 25);
            section3B.setAdviser(faculty3);
            section3B.setCurrentEnrollment(1); // Jane Davis
            section3B.setRoomAssignment("Room 302");
            
            // CS Sections
            Section sectionCS1A = new Section("CS-1A", "Computer Science 1A", "CS", 1, "2024-2025", 1, 30);
            sectionCS1A.setAdviser(faculty1);
            sectionCS1A.setCurrentEnrollment(0);
            sectionCS1A.setRoomAssignment("Room 401");
            
            Section sectionCS2A = new Section("CS-2A", "Computer Science 2A", "CS", 2, "2024-2025", 1, 28);
            sectionCS2A.setAdviser(faculty2);
            sectionCS2A.setCurrentEnrollment(0);
            sectionCS2A.setRoomAssignment("Room 501");
            
            sectionRepository.save(section1A);
            sectionRepository.save(section1B);
            sectionRepository.save(section2A);
            sectionRepository.save(section2B);
            sectionRepository.save(section3A);
            sectionRepository.save(section3B);
            sectionRepository.save(sectionCS1A);
            sectionRepository.save(sectionCS2A);
        }

        // Create sample IT subjects
        if (subjectRepository.count() == 0) {
            // First Year Subjects
            Subject prog1 = new Subject("IT-101", "Introduction to Programming", 3, "None", 1);
            Subject webDev1 = new Subject("IT-102", "Web Development Fundamentals", 3, "None", 1);
            Subject compFund = new Subject("IT-103", "Computer Fundamentals", 3, "None", 1);
            Subject math1 = new Subject("IT-104", "Mathematics for IT", 3, "None", 1);
            
            // Second Year Subjects
            Subject prog2 = new Subject("IT-201", "Object-Oriented Programming", 3, "IT-101", 2);
            Subject database1 = new Subject("IT-202", "Database Management Systems", 3, "IT-103", 2);
            Subject webDev2 = new Subject("IT-203", "Advanced Web Development", 3, "IT-102", 2);
            Subject networking = new Subject("IT-204", "Computer Networks", 3, "IT-103", 2);
            Subject dataStruct = new Subject("IT-205", "Data Structures and Algorithms", 3, "IT-101", 2);
            
            // Third Year Subjects
            Subject softEng = new Subject("IT-301", "Software Engineering", 3, "IT-201", 3);
            Subject database2 = new Subject("IT-302", "Advanced Database Systems", 3, "IT-202", 3);
            Subject webFramework = new Subject("IT-303", "Web Application Frameworks", 3, "IT-203", 3);
            Subject mobileDev = new Subject("IT-304", "Mobile Application Development", 3, "IT-201", 3);
            Subject cybersec = new Subject("IT-305", "Cybersecurity Fundamentals", 3, "IT-204", 3);
            Subject systemAnalysis = new Subject("IT-306", "System Analysis and Design", 3, "IT-301", 3);
            
            subjectRepository.save(prog1);
            subjectRepository.save(webDev1);
            subjectRepository.save(compFund);
            subjectRepository.save(math1);
            subjectRepository.save(prog2);
            subjectRepository.save(database1);
            subjectRepository.save(webDev2);
            subjectRepository.save(networking);
            subjectRepository.save(dataStruct);
            subjectRepository.save(softEng);
            subjectRepository.save(database2);
            subjectRepository.save(webFramework);
            subjectRepository.save(mobileDev);
            subjectRepository.save(cybersec);
            subjectRepository.save(systemAnalysis);
        }

        // Create sample schedules
        if (subjectScheduleRepository.count() == 0) {
            Faculty faculty1 = facultyRepository.findByFacultyId("FAC-001").orElse(null);
            Faculty faculty2 = facultyRepository.findByFacultyId("FAC-002").orElse(null);
            Faculty faculty3 = facultyRepository.findByFacultyId("FAC-003").orElse(null);
            Faculty faculty4 = facultyRepository.findByFacultyId("FAC-004").orElse(null);
            Faculty faculty5 = facultyRepository.findByFacultyId("FAC-005").orElse(null);
            
            // First Year Schedules
            Subject prog1 = subjectRepository.findBySubjectCode("IT-101").orElse(null);
            Subject webDev1 = subjectRepository.findBySubjectCode("IT-102").orElse(null);
            Subject compFund = subjectRepository.findBySubjectCode("IT-103").orElse(null);
            Subject math1 = subjectRepository.findBySubjectCode("IT-104").orElse(null);
            
            if (prog1 != null && faculty1 != null) {
                SubjectSchedule schedule1 = new SubjectSchedule(prog1, faculty1, "IT-1A", "Monday", 
                                                              LocalTime.of(8, 0), LocalTime.of(11, 0), "Room 101", 35);
                SubjectSchedule schedule2 = new SubjectSchedule(prog1, faculty1, "IT-1B", "Wednesday", 
                                                              LocalTime.of(13, 0), LocalTime.of(16, 0), "Room 102", 35);
                subjectScheduleRepository.save(schedule1);
                subjectScheduleRepository.save(schedule2);
            }
            
            if (webDev1 != null && faculty2 != null) {
                SubjectSchedule schedule3 = new SubjectSchedule(webDev1, faculty2, "IT-1A", "Tuesday", 
                                                              LocalTime.of(8, 0), LocalTime.of(11, 0), "Lab 201", 30);
                SubjectSchedule schedule4 = new SubjectSchedule(webDev1, faculty2, "IT-1B", "Thursday", 
                                                              LocalTime.of(13, 0), LocalTime.of(16, 0), "Lab 202", 30);
                subjectScheduleRepository.save(schedule3);
                subjectScheduleRepository.save(schedule4);
            }
            
            if (compFund != null && faculty3 != null) {
                SubjectSchedule schedule5 = new SubjectSchedule(compFund, faculty3, "IT-1A", "Friday", 
                                                              LocalTime.of(13, 0), LocalTime.of(16, 0), "Room 301", 40);
                SubjectSchedule schedule6 = new SubjectSchedule(compFund, faculty3, "IT-1B", "Friday", 
                                                              LocalTime.of(8, 0), LocalTime.of(11, 0), "Room 302", 40);
                subjectScheduleRepository.save(schedule5);
                subjectScheduleRepository.save(schedule6);
            }
            
            if (math1 != null && faculty4 != null) {
                SubjectSchedule schedule7 = new SubjectSchedule(math1, faculty4, "IT-1A", "Wednesday", 
                                                              LocalTime.of(13, 0), LocalTime.of(16, 0), "Room 401", 45);
                SubjectSchedule schedule8 = new SubjectSchedule(math1, faculty4, "IT-1B", "Thursday", 
                                                              LocalTime.of(8, 0), LocalTime.of(11, 0), "Room 402", 45);
                subjectScheduleRepository.save(schedule7);
                subjectScheduleRepository.save(schedule8);
            }
            
            // Second Year Schedules
            Subject prog2 = subjectRepository.findBySubjectCode("IT-201").orElse(null);
            Subject database1 = subjectRepository.findBySubjectCode("IT-202").orElse(null);
            Subject webDev2 = subjectRepository.findBySubjectCode("IT-203").orElse(null);
            Subject networking = subjectRepository.findBySubjectCode("IT-204").orElse(null);
            Subject dataStruct = subjectRepository.findBySubjectCode("IT-205").orElse(null);
            
            if (prog2 != null && faculty1 != null) {
                SubjectSchedule schedule9 = new SubjectSchedule(prog2, faculty1, "IT-2A", "Monday", 
                                                              LocalTime.of(8, 0), LocalTime.of(11, 0), "Room 501", 30);
                SubjectSchedule schedule10 = new SubjectSchedule(prog2, faculty1, "IT-2B", "Wednesday", 
                                                              LocalTime.of(13, 0), LocalTime.of(16, 0), "Room 502", 30);
                subjectScheduleRepository.save(schedule9);
                subjectScheduleRepository.save(schedule10);
            }
            
            if (database1 != null && faculty4 != null) {
                SubjectSchedule schedule11 = new SubjectSchedule(database1, faculty4, "IT-2A", "Tuesday", 
                                                              LocalTime.of(8, 0), LocalTime.of(11, 0), "Room 601", 25);
                SubjectSchedule schedule12 = new SubjectSchedule(database1, faculty4, "IT-2B", "Thursday", 
                                                              LocalTime.of(13, 0), LocalTime.of(16, 0), "Room 602", 25);
                subjectScheduleRepository.save(schedule11);
                subjectScheduleRepository.save(schedule12);
            }
            
            if (webDev2 != null && faculty2 != null) {
                SubjectSchedule schedule13 = new SubjectSchedule(webDev2, faculty2, "IT-2A", "Friday", 
                                                              LocalTime.of(13, 0), LocalTime.of(16, 0), "Lab 701", 20);
                SubjectSchedule schedule14 = new SubjectSchedule(webDev2, faculty2, "IT-2B", "Friday", 
                                                              LocalTime.of(8, 0), LocalTime.of(11, 0), "Lab 702", 20);
                subjectScheduleRepository.save(schedule13);
                subjectScheduleRepository.save(schedule14);
            }
            
            if (networking != null && faculty5 != null) {
                SubjectSchedule schedule15 = new SubjectSchedule(networking, faculty5, "IT-2A", "Wednesday", 
                                                              LocalTime.of(8, 0), LocalTime.of(11, 0), "Room 801", 28);
                SubjectSchedule schedule16 = new SubjectSchedule(networking, faculty5, "IT-2B", "Tuesday", 
                                                              LocalTime.of(13, 0), LocalTime.of(16, 0), "Room 802", 28);
                subjectScheduleRepository.save(schedule15);
                subjectScheduleRepository.save(schedule16);
            }
            
            if (dataStruct != null && faculty3 != null) {
                SubjectSchedule schedule17 = new SubjectSchedule(dataStruct, faculty3, "IT-2A", "Thursday", 
                                                              LocalTime.of(13, 0), LocalTime.of(16, 0), "Room 901", 25);
                SubjectSchedule schedule18 = new SubjectSchedule(dataStruct, faculty3, "IT-2B", "Monday", 
                                                              LocalTime.of(8, 0), LocalTime.of(11, 0), "Room 902", 25);
                subjectScheduleRepository.save(schedule17);
                subjectScheduleRepository.save(schedule18);
            }
            
            // Third Year Schedules
            Subject softEng = subjectRepository.findBySubjectCode("IT-301").orElse(null);
            Subject database2 = subjectRepository.findBySubjectCode("IT-302").orElse(null);
            Subject webFramework = subjectRepository.findBySubjectCode("IT-303").orElse(null);
            Subject mobileDev = subjectRepository.findBySubjectCode("IT-304").orElse(null);
            Subject cybersec = subjectRepository.findBySubjectCode("IT-305").orElse(null);
            
            if (softEng != null && faculty3 != null) {
                SubjectSchedule schedule19 = new SubjectSchedule(softEng, faculty3, "IT-3A", "Monday", 
                                                               LocalTime.of(8, 0), LocalTime.of(11, 0), "Room 1001", 30);
                SubjectSchedule schedule20 = new SubjectSchedule(softEng, faculty3, "IT-3B", "Tuesday", 
                                                               LocalTime.of(13, 0), LocalTime.of(16, 0), "Room 1002", 30);
                subjectScheduleRepository.save(schedule19);
                subjectScheduleRepository.save(schedule20);
            }
            
            if (database2 != null && faculty4 != null) {
                SubjectSchedule schedule21 = new SubjectSchedule(database2, faculty4, "IT-3A", "Tuesday", 
                                                               LocalTime.of(8, 0), LocalTime.of(11, 0), "Lab 1001", 20);
                SubjectSchedule schedule22 = new SubjectSchedule(database2, faculty4, "IT-3B", "Wednesday", 
                                                               LocalTime.of(8, 0), LocalTime.of(11, 0), "Lab 1002", 20);
                subjectScheduleRepository.save(schedule21);
                subjectScheduleRepository.save(schedule22);
            }
            
            if (webFramework != null && faculty2 != null) {
                SubjectSchedule schedule23 = new SubjectSchedule(webFramework, faculty2, "IT-3A", "Wednesday", 
                                                               LocalTime.of(13, 0), LocalTime.of(16, 0), "Lab 1003", 22);
                SubjectSchedule schedule24 = new SubjectSchedule(webFramework, faculty2, "IT-3B", "Thursday", 
                                                               LocalTime.of(13, 0), LocalTime.of(16, 0), "Lab 1004", 22);
                subjectScheduleRepository.save(schedule23);
                subjectScheduleRepository.save(schedule24);
            }
            
            if (mobileDev != null && faculty1 != null) {
                SubjectSchedule schedule25 = new SubjectSchedule(mobileDev, faculty1, "IT-3A", "Thursday", 
                                                               LocalTime.of(8, 0), LocalTime.of(11, 0), "Lab 1005", 25);
                SubjectSchedule schedule26 = new SubjectSchedule(mobileDev, faculty1, "IT-3B", "Friday", 
                                                               LocalTime.of(8, 0), LocalTime.of(11, 0), "Lab 1006", 25);
                subjectScheduleRepository.save(schedule25);
                subjectScheduleRepository.save(schedule26);
            }
            
            if (cybersec != null && faculty5 != null) {
                SubjectSchedule schedule27 = new SubjectSchedule(cybersec, faculty5, "IT-3A", "Friday", 
                                                               LocalTime.of(13, 0), LocalTime.of(16, 0), "Room 1007", 30);
                SubjectSchedule schedule28 = new SubjectSchedule(cybersec, faculty5, "IT-3B", "Monday", 
                                                               LocalTime.of(13, 0), LocalTime.of(16, 0), "Room 1008", 30);
                subjectScheduleRepository.save(schedule27);
                subjectScheduleRepository.save(schedule28);
            }
        }

        // Create enrollment period
        if (enrollmentPeriodRepository.count() == 0) {
            EnrollmentPeriod period = new EnrollmentPeriod(
                "1st Semester", 
                "2024-2025", 
                LocalDateTime.now().minusDays(1), 
                LocalDateTime.now().plusDays(30), 
                true
            );
            enrollmentPeriodRepository.save(period);
        }

        // Create sample grades for demonstration
        if (gradeRepository.count() == 0) {
            Student student6 = studentRepository.findByStudentId("2022-0001").orElse(null);
            Faculty faculty1 = facultyRepository.findByFacultyId("FAC-001").orElse(null);
            Subject prog1 = subjectRepository.findBySubjectCode("IT-101").orElse(null);
            Subject webDev1 = subjectRepository.findBySubjectCode("IT-102").orElse(null);
            
            if (student6 != null && faculty1 != null && prog1 != null) {
                Grade grade1 = new Grade(student6, prog1, faculty1, "IT-1A", "1st Semester", "2023-2024");
                grade1.setMidtermGrade(85.0);
                grade1.setFinalGrade(88.0);
                grade1.calculateFinalRating();
                gradeRepository.save(grade1);
            }
            
            if (student6 != null && faculty1 != null && webDev1 != null) {
                Grade grade2 = new Grade(student6, webDev1, faculty1, "IT-1B", "1st Semester", "2023-2024");
                grade2.setMidtermGrade(90.0);
                grade2.setFinalGrade(92.0);
                grade2.calculateFinalRating();
                gradeRepository.save(grade2);
            }
        }
        
        } catch (Exception e) {
            System.err.println("ERROR in DataInitializer: " + e.getMessage());
        }
    }
    
    private void initializeDefaultAccounts() {
        // Create default admin if not exists
        if (adminRepository.findByAdminId("admin001").isEmpty()) {
            Admin admin = new Admin();
            admin.setAdminId("admin001");
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setEmail("admin@bmw-sis.edu");
            admin.setPassword(passwordEncoder.encode("admin123"));
            adminRepository.save(admin);
        }
        
        // Create default faculty if not exists
        if (facultyRepository.findByFacultyId("FAC001").isEmpty()) {
            Faculty faculty = new Faculty();
            faculty.setFacultyId("FAC001");
            faculty.setFirstName("Dr. John");
            faculty.setLastName("Smith");
            faculty.setEmail("john.smith@bmw-sis.edu");
            faculty.setDepartment("Computer Science");
            faculty.setPassword(passwordEncoder.encode("faculty123"));
            facultyRepository.save(faculty);
        }
        
        // Create another faculty
        if (facultyRepository.findByFacultyId("FAC002").isEmpty()) {
            Faculty faculty = new Faculty();
            faculty.setFacultyId("FAC002");
            faculty.setFirstName("Dr. Sarah");
            faculty.setLastName("Johnson");
            faculty.setEmail("sarah.johnson@bmw-sis.edu");
            faculty.setDepartment("Mathematics");
            faculty.setPassword(passwordEncoder.encode("faculty123"));
            facultyRepository.save(faculty);
        }
    }

    private void initializeAdminAccounts() {
        // Check if admin accounts already exist
        if (adminRepository.count() == 0) {
            
            // Create default admin account
            Admin defaultAdmin = new Admin();
            defaultAdmin.setAdminId("admin001");
            defaultAdmin.setFirstName("System");
            defaultAdmin.setLastName("Administrator");
            defaultAdmin.setEmail("admin@bmw-sis.edu");
            defaultAdmin.setPassword(passwordEncoder.encode("admin123"));
            defaultAdmin.setRole("ADMIN");
            
            adminRepository.save(defaultAdmin);
            
            // Create additional admin account
            Admin secondAdmin = new Admin();
            secondAdmin.setAdminId("admin002");
            secondAdmin.setFirstName("BMW");
            secondAdmin.setLastName("Admin");
            secondAdmin.setEmail("bmwadmin@bmw-sis.edu");
            secondAdmin.setPassword(passwordEncoder.encode("bmw123"));
            secondAdmin.setRole("ADMIN");
            
            adminRepository.save(secondAdmin);
            
        }
    }
} 