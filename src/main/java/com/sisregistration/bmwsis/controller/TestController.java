package com.sisregistration.bmwsis.controller;

import com.sisregistration.bmwsis.service.PasswordMigrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private PasswordMigrationService passwordMigrationService;

    /**
     * Test endpoint to verify password hashing is working
     * Access: http://localhost:5521/test-password-hashing
     */
    @GetMapping("/test-password-hashing")
    @ResponseBody
    public String testPasswordHashing() {
        StringBuilder result = new StringBuilder();
        
        // Test password hashing
        String plainPassword = "password123";
        String hashedPassword = passwordEncoder.encode(plainPassword);
        boolean matches = passwordEncoder.matches(plainPassword, hashedPassword);
        
        result.append("=== Password Hashing Test ===\n");
        result.append("Plain password: ").append(plainPassword).append("\n");
        result.append("Hashed password: ").append(hashedPassword).append("\n");
        result.append("Hash length: ").append(hashedPassword.length()).append("\n");
        result.append("Passwords match: ").append(matches).append("\n");
        result.append("Is BCrypt format: ").append(hashedPassword.startsWith("$2")).append("\n\n");
        
        // Test with another password
        String testPassword = "admin123";
        String testHashed = passwordEncoder.encode(testPassword);
        boolean testMatches = passwordEncoder.matches(testPassword, testHashed);
        
        result.append("=== Second Test ===\n");
        result.append("Test password: ").append(testPassword).append("\n");
        result.append("Test hashed: ").append(testHashed).append("\n");
        result.append("Test matches: ").append(testMatches).append("\n\n");
        
        // Get migration status
        PasswordMigrationService.MigrationStatus status = passwordMigrationService.getMigrationStatus();
        result.append("=== Database Migration Status ===\n");
        result.append(status.toString()).append("\n\n");
        
        result.append("✅ Password hashing is working correctly!");
        
        return result.toString().replace("\n", "<br>");
    }
    
    /**
     * Trigger password migration for existing plain text passwords
     * Access: http://localhost:5521/migrate-passwords
     */
    @GetMapping("/migrate-passwords")
    @ResponseBody
    public String migratePasswords() {
        StringBuilder result = new StringBuilder();
        
        result.append("=== Password Migration Started ===<br>");
        
        try {
            int studentsMigrated = passwordMigrationService.migrateStudentPasswords();
            int facultyMigrated = passwordMigrationService.migrateFacultyPasswords();
            int adminsMigrated = passwordMigrationService.migrateAdminPasswords();
            
            result.append("Students migrated: ").append(studentsMigrated).append("<br>");
            result.append("Faculty migrated: ").append(facultyMigrated).append("<br>");
            result.append("Admins migrated: ").append(adminsMigrated).append("<br><br>");
            
            PasswordMigrationService.MigrationStatus status = passwordMigrationService.getMigrationStatus();
            result.append("=== Final Status ===<br>");
            result.append(status.toString().replace("\n", "<br>")).append("<br><br>");
            
            result.append("✅ Migration completed successfully!");
            
        } catch (Exception e) {
            result.append("❌ Migration failed: ").append(e.getMessage());
        }
        
        return result.toString();
    }
} 