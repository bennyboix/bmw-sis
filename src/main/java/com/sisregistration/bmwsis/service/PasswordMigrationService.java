package com.sisregistration.bmwsis.service;

import com.sisregistration.bmwsis.entity.*;
import com.sisregistration.bmwsis.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PasswordMigrationService {

    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private FacultyRepository facultyRepository;
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Migrate all existing plain text passwords to BCrypt hashed passwords
     * This should be run once when implementing Spring Security
     */
    @Transactional
    public void migrateAllPasswords() {
        migrateStudentPasswords();
        migrateFacultyPasswords();
        migrateAdminPasswords();
    }

    /**
     * Migrate student passwords - only if they appear to be plain text
     */
    @Transactional
    public int migrateStudentPasswords() {
        List<Student> students = studentRepository.findAll();
        int migratedCount = 0;
        
        for (Student student : students) {
            if (isPlainTextPassword(student.getPassword())) {
                String hashedPassword = passwordEncoder.encode(student.getPassword());
                student.setPassword(hashedPassword);
                studentRepository.save(student);
                migratedCount++;
                System.out.println("Migrated password for student: " + student.getStudentId());
            }
        }
        
        System.out.println("Migrated " + migratedCount + " student passwords");
        return migratedCount;
    }

    /**
     * Migrate faculty passwords - only if they appear to be plain text
     */
    @Transactional
    public int migrateFacultyPasswords() {
        List<Faculty> faculties = facultyRepository.findAll();
        int migratedCount = 0;
        
        for (Faculty faculty : faculties) {
            if (isPlainTextPassword(faculty.getPassword())) {
                String hashedPassword = passwordEncoder.encode(faculty.getPassword());
                faculty.setPassword(hashedPassword);
                facultyRepository.save(faculty);
                migratedCount++;
                System.out.println("Migrated password for faculty: " + faculty.getFacultyId());
            }
        }
        
        System.out.println("Migrated " + migratedCount + " faculty passwords");
        return migratedCount;
    }

    /**
     * Migrate admin passwords - only if they appear to be plain text
     */
    @Transactional
    public int migrateAdminPasswords() {
        List<Admin> admins = adminRepository.findAll();
        int migratedCount = 0;
        
        for (Admin admin : admins) {
            if (isPlainTextPassword(admin.getPassword())) {
                String hashedPassword = passwordEncoder.encode(admin.getPassword());
                admin.setPassword(hashedPassword);
                adminRepository.save(admin);
                migratedCount++;
                System.out.println("Migrated password for admin: " + admin.getAdminId());
            }
        }
        
        System.out.println("Migrated " + migratedCount + " admin passwords");
        return migratedCount;
    }

    /**
     * Check if a password appears to be plain text (not BCrypt hashed)
     * BCrypt hashes start with $2a$, $2b$, $2x$, or $2y$ and are 60 characters long
     */
    private boolean isPlainTextPassword(String password) {
        if (password == null || password.length() != 60) {
            return true; // Likely plain text
        }
        
        return !password.startsWith("$2a$") && 
               !password.startsWith("$2b$") && 
               !password.startsWith("$2x$") && 
               !password.startsWith("$2y$");
    }

    /**
     * Test password hashing - useful for debugging
     */
    public void testPasswordHashing() {
        String plainPassword = "password123";
        String hashedPassword = passwordEncoder.encode(plainPassword);
        boolean matches = passwordEncoder.matches(plainPassword, hashedPassword);
        
        System.out.println("=== Password Hashing Test ===");
        System.out.println("Plain password: " + plainPassword);
        System.out.println("Hashed password: " + hashedPassword);
        System.out.println("Passwords match: " + matches);
        System.out.println("Hash length: " + hashedPassword.length());
        System.out.println("Is plain text: " + isPlainTextPassword(hashedPassword));
    }

    /**
     * Get migration status for all user types
     */
    public MigrationStatus getMigrationStatus() {
        List<Student> students = studentRepository.findAll();
        List<Faculty> faculties = facultyRepository.findAll();
        List<Admin> admins = adminRepository.findAll();
        
        int plainStudents = 0;
        int hashedStudents = 0;
        for (Student student : students) {
            if (isPlainTextPassword(student.getPassword())) {
                plainStudents++;
            } else {
                hashedStudents++;
            }
        }
        
        int plainFaculties = 0;
        int hashedFaculties = 0;
        for (Faculty faculty : faculties) {
            if (isPlainTextPassword(faculty.getPassword())) {
                plainFaculties++;
            } else {
                hashedFaculties++;
            }
        }
        
        int plainAdmins = 0;
        int hashedAdmins = 0;
        for (Admin admin : admins) {
            if (isPlainTextPassword(admin.getPassword())) {
                plainAdmins++;
            } else {
                hashedAdmins++;
            }
        }
        
        return new MigrationStatus(
            plainStudents, hashedStudents,
            plainFaculties, hashedFaculties,
            plainAdmins, hashedAdmins
        );
    }

    /**
     * Migration status DTO
     */
    public static class MigrationStatus {
        private final int plainStudents;
        private final int hashedStudents;
        private final int plainFaculties;
        private final int hashedFaculties;
        private final int plainAdmins;
        private final int hashedAdmins;

        public MigrationStatus(int plainStudents, int hashedStudents, 
                             int plainFaculties, int hashedFaculties,
                             int plainAdmins, int hashedAdmins) {
            this.plainStudents = plainStudents;
            this.hashedStudents = hashedStudents;
            this.plainFaculties = plainFaculties;
            this.hashedFaculties = hashedFaculties;
            this.plainAdmins = plainAdmins;
            this.hashedAdmins = hashedAdmins;
        }

        public int getPlainStudents() { return plainStudents; }
        public int getHashedStudents() { return hashedStudents; }
        public int getPlainFaculties() { return plainFaculties; }
        public int getHashedFaculties() { return hashedFaculties; }
        public int getPlainAdmins() { return plainAdmins; }
        public int getHashedAdmins() { return hashedAdmins; }
        
        public int getTotalPlain() { return plainStudents + plainFaculties + plainAdmins; }
        public int getTotalHashed() { return hashedStudents + hashedFaculties + hashedAdmins; }
        public int getTotalUsers() { return getTotalPlain() + getTotalHashed(); }
        
        public boolean isFullyMigrated() { return getTotalPlain() == 0; }
        
        @Override
        public String toString() {
            return String.format(
                "Migration Status:\n" +
                "Students: %d plain, %d hashed\n" +
                "Faculty: %d plain, %d hashed\n" +
                "Admins: %d plain, %d hashed\n" +
                "Total: %d plain, %d hashed (%s)",
                plainStudents, hashedStudents,
                plainFaculties, hashedFaculties,
                plainAdmins, hashedAdmins,
                getTotalPlain(), getTotalHashed(),
                isFullyMigrated() ? "COMPLETE" : "INCOMPLETE"
            );
        }
    }
} 