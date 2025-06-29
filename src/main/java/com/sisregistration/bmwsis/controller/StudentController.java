package com.sisregistration.bmwsis.controller;
import com.sisregistration.bmwsis.entity.*;
import com.sisregistration.bmwsis.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/student")
public class StudentController {
    
    @Autowired
    private StudentService studentService;
    
    @GetMapping("/login")
    public String showLoginPage() {
        return "student-login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String studentId, 
                       @RequestParam String password,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        
        Optional<Student> studentOpt = studentService.authenticateStudent(studentId, password);
        
        if (studentOpt.isPresent()) {
            session.setAttribute("loggedInStudent", studentOpt.get());
            return "redirect:/student/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid student ID or password");
            return "redirect:/student/login";
        }
    }
    
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Student student = (Student) session.getAttribute("loggedInStudent");
        if (student == null) {
            return "redirect:/student/login";
        }
        
        model.addAttribute("student", student);
        return "student-dashboard";
    }
    
    @GetMapping("/grades")
    public String viewGrades(HttpSession session, Model model,
                           @RequestParam(value = "semester", defaultValue = "current") String semesterFilter) {
        Student student = (Student) session.getAttribute("loggedInStudent");
        if (student == null) {
            return "redirect:/student/login";
        }
        
        List<Grade> grades;
        if ("current".equals(semesterFilter)) {
            grades = studentService.getCurrentSemesterGrades(student);
        } else {
            grades = studentService.getStudentGradesWithDetails(student);
        }
        
        // Get grade statistics
        StudentService.StudentGradeStatistics stats = studentService.getStudentGradeStatistics(student);
        
        model.addAttribute("student", student);
        model.addAttribute("grades", grades);
        model.addAttribute("stats", stats);
        model.addAttribute("semesterFilter", semesterFilter);
        
        return "student-grades";
    }
    
    @GetMapping("/schedule")
    public String viewSchedule(HttpSession session, Model model) {
        Student student = (Student) session.getAttribute("loggedInStudent");
        if (student == null) {
            return "redirect:/student/login";
        }
        
        // Use current semester schedules to show subjects even if graded
        List<Enrollment> enrollments = studentService.getCurrentSemesterSchedules(student);
        model.addAttribute("student", student);
        model.addAttribute("enrollments", enrollments);
        return "student-schedule";
    }
    
    @GetMapping("/enroll")
    public String showEnrollmentPage(HttpSession session, Model model) {
        Student student = (Student) session.getAttribute("loggedInStudent");
        if (student == null) {
            return "redirect:/student/login";
        }
        
        try {
            boolean enrollmentOpen = studentService.isEnrollmentOpen();
            List<StudentService.ScheduleDisplayInfo> availableSchedules = studentService.getAvailableSchedulesWithRealTimeData(student);
            
            model.addAttribute("student", student);
            model.addAttribute("enrollmentOpen", enrollmentOpen);
            model.addAttribute("availableSchedules", availableSchedules);
            return "student-enrollment";
        } catch (Exception e) {
            System.err.println("Error loading enrollment page: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error loading enrollment page. Please try again or contact support.");
            model.addAttribute("student", student);
            model.addAttribute("enrollmentOpen", false);
            model.addAttribute("availableSchedules", new java.util.ArrayList<>());
            return "student-enrollment";
        }
    }
    
    @PostMapping("/enroll")
    public String enrollInSubject(@RequestParam Long scheduleId,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Student student = (Student) session.getAttribute("loggedInStudent");
        if (student == null) {
            return "redirect:/student/login";
        }
        
        try {
            StudentService.EnrollmentResult result = studentService.enrollStudentWithDetails(student, scheduleId);
            
            if (result.isSuccess()) {
                redirectAttributes.addFlashAttribute("success", result.getMessage());
            } else {
                redirectAttributes.addFlashAttribute("error", result.getMessage());
            }
            
            return "redirect:/student/enroll";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error enrolling in subject: " + e.getMessage());
            return "redirect:/student/enroll";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/student/login";
    }
} 