package com.sisregistration.bmwsis.repository;

import com.sisregistration.bmwsis.entity.Enrollment;
import com.sisregistration.bmwsis.entity.Student;
import com.sisregistration.bmwsis.entity.Subject;
import com.sisregistration.bmwsis.entity.SubjectSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudent(Student student);
    List<Enrollment> findByStudentAndStatus(Student student, String status);
    
    @Query("SELECT e FROM Enrollment e WHERE e.student = :student AND e.status = 'ENROLLED'")
    List<Enrollment> findCurrentEnrollmentsByStudent(@Param("student") Student student);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.subjectSchedule.id = :scheduleId AND e.status = 'ENROLLED'")
    Long countEnrolledStudentsBySchedule(@Param("scheduleId") Long scheduleId);
    
    // Check if student is already enrolled in the same subject
    @Query("SELECT e FROM Enrollment e WHERE e.student = :student AND e.subject = :subject AND e.status = 'ENROLLED'")
    Optional<Enrollment> findActiveEnrollmentByStudentAndSubject(@Param("student") Student student, @Param("subject") Subject subject);
    
    // Check if student is already enrolled in the specific schedule
    @Query("SELECT e FROM Enrollment e WHERE e.student = :student AND e.subjectSchedule = :schedule AND e.status = 'ENROLLED'")
    Optional<Enrollment> findActiveEnrollmentByStudentAndSchedule(@Param("student") Student student, @Param("schedule") SubjectSchedule schedule);
    
    // Find all enrollments for a specific schedule
    @Query("SELECT e FROM Enrollment e WHERE e.subjectSchedule.id = :scheduleId")
    List<Enrollment> findByScheduleId(@Param("scheduleId") Long scheduleId);
    
    // Find all enrollments with eager fetching to avoid lazy loading issues
    @Query("SELECT e FROM Enrollment e " +
           "LEFT JOIN FETCH e.student s " +
           "LEFT JOIN FETCH e.subject sub " +
           "LEFT JOIN FETCH e.subjectSchedule ss " +
           "LEFT JOIN FETCH ss.faculty f " +
           "ORDER BY e.enrollmentDate DESC")
    List<Enrollment> findAllWithDetails();
    
    // Find current semester enrollments (including graded subjects, excluding only DROPPED)
    @Query("SELECT e FROM Enrollment e WHERE e.student = :student " +
           "AND e.semester = :semester AND e.academicYear = :academicYear " +
           "AND e.status != 'DROPPED' " +
           "ORDER BY e.subject.subjectCode ASC")
    List<Enrollment> findCurrentSemesterEnrollmentsByStudent(@Param("student") Student student, 
                                                           @Param("semester") String semester, 
                                                           @Param("academicYear") String academicYear);
} 