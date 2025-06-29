package com.sisregistration.bmwsis.controller;

import com.sisregistration.bmwsis.entity.*;
import com.sisregistration.bmwsis.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentRestController {
    @Autowired
    private StudentService studentService;

    @PostMapping("/login")
    public Student login(@RequestBody Map<String, String> payload) {
        String studentId = payload.get("studentId");
        String password = payload.get("password");
        return studentService.authenticateStudent(studentId, password).orElse(null);
    }

    @GetMapping("/{studentId}/grades")
    public List<Grade> getGrades(@PathVariable String studentId) {
        Student student = studentService.findByStudentId(studentId);
        return student != null ? studentService.getStudentGrades(student) : null;
    }

    @GetMapping("/{studentId}/schedule")
    public List<Enrollment> getSchedule(@PathVariable String studentId) {
        Student student = studentService.findByStudentId(studentId);
        return student != null ? studentService.getStudentSchedules(student) : null;
    }

    @GetMapping("/{studentId}/available-schedules")
    public List<SubjectSchedule> getAvailableSchedules(@PathVariable String studentId) {
        Student student = studentService.findByStudentId(studentId);
        return student != null ? studentService.getAvailableSchedulesForStudent(student) : null;
    }

    @PostMapping("/{studentId}/enroll")
    public String enroll(@PathVariable String studentId, @RequestBody Map<String, Long> payload) {
        Student student = studentService.findByStudentId(studentId);
        Long scheduleId = payload.get("scheduleId");
        if (student == null || scheduleId == null) return "Student or schedule not found";
        boolean success = studentService.enrollStudent(student, scheduleId);
        return success ? "Enrolled successfully" : "Enrollment failed";
    }
}
