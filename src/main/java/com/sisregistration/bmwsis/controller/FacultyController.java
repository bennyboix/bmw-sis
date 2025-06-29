package com.sisregistration.bmwsis.controller;

import com.sisregistration.bmwsis.entity.Faculty;
import com.sisregistration.bmwsis.entity.SubjectSchedule;
import com.sisregistration.bmwsis.entity.Student;
import com.sisregistration.bmwsis.entity.Enrollment;
import com.sisregistration.bmwsis.entity.Grade;
import com.sisregistration.bmwsis.entity.Subject;
import com.sisregistration.bmwsis.service.FacultyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/faculty")
public class FacultyController {
    
    @Autowired
    private FacultyService facultyService;
    
    @GetMapping("/login")
    public String showLoginPage() {
        return "faculty-login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String facultyId, 
                       @RequestParam String password,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        
        Optional<Faculty> facultyOpt = facultyService.authenticateFaculty(facultyId, password);
        
        if (facultyOpt.isPresent()) {
            session.setAttribute("loggedInFaculty", facultyOpt.get());
            return "redirect:/faculty/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid faculty ID or password");
            return "redirect:/faculty/login";
        }
    }
    
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Faculty faculty = (Faculty) session.getAttribute("loggedInFaculty");
        
        if (faculty == null) {
            return "redirect:/faculty/login";
        }
        
        try {
            // Get faculty assignments and teaching load
            List<SubjectSchedule> schedules = facultyService.getFacultySchedules(faculty);
            
            // Get assigned sections with better formatting
            Set<String> assignedSections = schedules.stream()
                .map(SubjectSchedule::getSectionCode)
                .collect(Collectors.toSet());
            
            FacultyService.FacultyAssignmentSummary assignmentSummary = facultyService.getFacultyAssignmentSummary(faculty);
            List<SubjectSchedule> todaySchedule = facultyService.getTodaySchedule(faculty);
            Optional<SubjectSchedule> nextClass = facultyService.getNextClass(faculty);
            
            model.addAttribute("faculty", faculty);
            model.addAttribute("schedules", schedules);
            model.addAttribute("assignmentSummary", assignmentSummary);
            model.addAttribute("assignedSections", assignedSections);
            model.addAttribute("todaySchedule", todaySchedule);
            model.addAttribute("nextClass", nextClass.orElse(null));
            return "faculty-dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading dashboard: " + e.getMessage());
            return "faculty-dashboard";
        }
    }
    
    @GetMapping("/classes")
    public String viewClasses(HttpSession session, Model model, 
                             @RequestParam(value = "view", defaultValue = "program") String viewType) {
        Faculty faculty = (Faculty) session.getAttribute("loggedInFaculty");
        if (faculty == null) {
            return "redirect:/faculty/login";
        }
        
        List<SubjectSchedule> schedules = facultyService.getFacultySchedules(faculty);
        java.util.Map<String, List<SubjectSchedule>> schedulesBySection = facultyService.getFacultySchedulesBySection(faculty);
        java.util.Map<String, List<SubjectSchedule>> schedulesByProgram = facultyService.getFacultySchedulesByProgram(faculty);
        java.util.Map<String, List<SubjectSchedule>> schedulesByDay = facultyService.getFacultySchedulesByDay(faculty);
        List<String> assignedSections = facultyService.getFacultySections(faculty);
        List<SubjectSchedule> todaySchedule = facultyService.getTodaySchedule(faculty);
        Optional<SubjectSchedule> nextClass = facultyService.getNextClass(faculty);
        FacultyService.ProgramTeachingLoad programLoad = facultyService.getProgramTeachingLoad(faculty);
        
        model.addAttribute("faculty", faculty);
        model.addAttribute("schedules", schedules);
        model.addAttribute("schedulesBySection", schedulesBySection);
        model.addAttribute("schedulesByProgram", schedulesByProgram);
        model.addAttribute("schedulesByDay", schedulesByDay);
        model.addAttribute("assignedSections", assignedSections);
        model.addAttribute("todaySchedule", todaySchedule);
        model.addAttribute("nextClass", nextClass.orElse(null));
        model.addAttribute("programLoad", programLoad);
        model.addAttribute("currentView", viewType);
        
        return "faculty-classes";
    }
    
    @GetMapping("/class/{scheduleId}/students")
    public String viewClassStudents(@PathVariable Long scheduleId, 
                                   HttpSession session, 
                                   Model model) {
        Faculty faculty = (Faculty) session.getAttribute("loggedInFaculty");
        if (faculty == null) {
            return "redirect:/faculty/login";
        }
        
        Optional<SubjectSchedule> scheduleOpt = facultyService.getScheduleById(scheduleId);
        if (scheduleOpt.isPresent()) {
            List<Enrollment> enrollments = facultyService.getStudentsInSchedule(scheduleId);
            model.addAttribute("faculty", faculty);
            model.addAttribute("schedule", scheduleOpt.get());
            model.addAttribute("enrollments", enrollments);
            return "faculty-class-students";
        }
        
        return "redirect:/faculty/classes";
    }
    
    @GetMapping("/grades")
    public String viewGrades(HttpSession session, Model model,
                           @RequestParam(value = "subject", required = false) Long subjectId,
                           @RequestParam(value = "section", required = false) String sectionCode) {
        Faculty faculty = (Faculty) session.getAttribute("loggedInFaculty");
        if (faculty == null) {
            return "redirect:/faculty/login";
        }
        
        try {
        // Get grades with auto-creation for enrolled students
        List<Grade> grades;
        if (subjectId != null) {
            grades = facultyService.getFacultyGradesBySubject(faculty, subjectId);
        } else if (sectionCode != null) {
            grades = facultyService.getFacultyGradesBySection(faculty, sectionCode);
        } else {
            grades = facultyService.getFacultyGradesWithAutoCreation(faculty);
        }
        
        // Get filtering options
        List<Subject> subjects = facultyService.getFacultySubjects(faculty);
        List<String> sections = facultyService.getFacultyUniqueSections(faculty);
        
        // Get grade statistics
        FacultyService.GradeStatistics stats = facultyService.getFacultyGradeStatistics(faculty);
        
        model.addAttribute("faculty", faculty);
        model.addAttribute("grades", grades);
        model.addAttribute("subjects", subjects);
        model.addAttribute("sections", sections);
        model.addAttribute("stats", stats);
        model.addAttribute("selectedSubject", subjectId);
        model.addAttribute("selectedSection", sectionCode);
        
        return "faculty-grades";
        } catch (Exception e) {
            System.err.println("Error in faculty grades: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error loading grades: " + e.getMessage());
            model.addAttribute("faculty", faculty);
            return "faculty-grades";
        }
    }
    
    @GetMapping("/students")
    public String viewStudents(HttpSession session, Model model) {
        Faculty faculty = (Faculty) session.getAttribute("loggedInFaculty");
        if (faculty == null) {
            return "redirect:/faculty/login";
        }
        
        try {
            // Get all students enrolled in faculty's classes
            List<Student> students = facultyService.getFacultyStudents(faculty);
            System.out.println("=== FACULTY STUDENTS DEBUG ===");
            System.out.println("Faculty: " + faculty.getFacultyId() + " (" + faculty.getFirstName() + " " + faculty.getLastName() + ")");
            System.out.println("Students retrieved: " + students.size());
            for (Student student : students) {
                System.out.println("  - " + student.getStudentId() + ": " + student.getFirstName() + " " + student.getLastName());
            }
            
            // Get student statistics
            FacultyService.StudentStatistics stats = facultyService.getFacultyStudentStatistics(faculty);
            System.out.println("Statistics - Total: " + stats.getTotalStudents() + 
                             ", Active: " + stats.getActiveStudents() + 
                             ", Average: " + stats.getAverageGrade());
            
            // Get filtering options
            List<Subject> subjects = facultyService.getFacultySubjects(faculty);
            List<String> sections = facultyService.getFacultyUniqueSections(faculty);
            System.out.println("Subjects available: " + subjects.size());
            System.out.println("Sections available: " + sections.size());
            
            model.addAttribute("faculty", faculty);
            model.addAttribute("students", students);
            model.addAttribute("stats", stats);
            model.addAttribute("subjects", subjects);
            model.addAttribute("sections", sections);
            
            System.out.println("=== END DEBUG ===");
            return "faculty-students";
        } catch (Exception e) {
            System.err.println("Error in faculty students: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error loading students: " + e.getMessage());
            model.addAttribute("faculty", faculty);
            return "faculty-students";
        }
    }
    
    @GetMapping("/schedule")
    public String viewSchedule(HttpSession session, Model model) {
        Faculty faculty = (Faculty) session.getAttribute("loggedInFaculty");
        if (faculty == null) {
            return "redirect:/faculty/login";
        }
        
        try {
            List<SubjectSchedule> schedules = facultyService.getFacultySchedules(faculty);
            model.addAttribute("faculty", faculty);
            model.addAttribute("schedules", schedules);
            return "faculty-schedule";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading schedule: " + e.getMessage());
            model.addAttribute("faculty", faculty);
            return "faculty-schedule";
        }
    }
    
    @PostMapping("/grade/update")
    public String updateGrade(@RequestParam Long gradeId,
                             @RequestParam Double gradeValue,
                             @RequestParam String remarks,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        Faculty faculty = (Faculty) session.getAttribute("loggedInFaculty");
        if (faculty == null) {
            return "redirect:/faculty/login";
        }
        
        try {
            facultyService.updateGrade(gradeId, gradeValue, remarks);
            redirectAttributes.addFlashAttribute("success", "Grade updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update grade: " + e.getMessage());
        }
        
        return "redirect:/faculty/grades";
    }
    
    // AJAX Endpoints for Modern Grade Management
    
    @PostMapping("/grades/update-individual")
    @ResponseBody
    public org.springframework.http.ResponseEntity<String> updateIndividualGrade(@RequestParam Long gradeId,
                                                                               @RequestParam(required = false) Double midtermGrade,
                                                                               @RequestParam(required = false) Double finalGrade,
                                                                               HttpSession session) {
        Faculty faculty = (Faculty) session.getAttribute("loggedInFaculty");
        if (faculty == null) {
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        
        try {
            facultyService.updateIndividualGrade(gradeId, midtermGrade, finalGrade);
            return org.springframework.http.ResponseEntity.ok("Grade updated successfully");
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                                                         .body("Error updating grade: " + e.getMessage());
        }
    }
    
    @PostMapping("/grades/batch-update")
    @ResponseBody
    public org.springframework.http.ResponseEntity<String> batchUpdateGrades(@RequestBody List<FacultyService.GradeUpdateRequest> gradeUpdates,
                                                                           HttpSession session) {
        Faculty faculty = (Faculty) session.getAttribute("loggedInFaculty");
        if (faculty == null) {
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        
        try {
            facultyService.batchUpdateGrades(gradeUpdates);
            return org.springframework.http.ResponseEntity.ok("All grades updated successfully");
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                                                         .body("Error updating grades: " + e.getMessage());
        }
    }
    
    @GetMapping("/grades/statistics")
    @ResponseBody
    public org.springframework.http.ResponseEntity<FacultyService.GradeStatistics> getGradeStatistics(HttpSession session) {
        Faculty faculty = (Faculty) session.getAttribute("loggedInFaculty");
        if (faculty == null) {
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            FacultyService.GradeStatistics stats = facultyService.getFacultyGradeStatistics(faculty);
            return org.springframework.http.ResponseEntity.ok(stats);
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/faculty/login";
    }
} 