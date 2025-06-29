package com.sisregistration.bmwsis.repository;

import com.sisregistration.bmwsis.entity.SubjectSchedule;
import com.sisregistration.bmwsis.entity.Subject;
import com.sisregistration.bmwsis.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubjectScheduleRepository extends JpaRepository<SubjectSchedule, Long> {
    List<SubjectSchedule> findBySubject(Subject subject);
    
    // Add method to find schedules by section code
    List<SubjectSchedule> findBySectionCode(String sectionCode);
    
    // Add method to find schedules by faculty ID
    List<SubjectSchedule> findByFacultyId(Long facultyId);
    
    @Query("SELECT ss FROM SubjectSchedule ss WHERE ss.subject.yearLevel = :yearLevel " +
           "AND (SELECT COUNT(e) FROM Enrollment e WHERE e.subjectSchedule = ss AND e.status = 'ENROLLED') < ss.maxSlots")
    List<SubjectSchedule> findAvailableSchedulesForYearLevel(@Param("yearLevel") Integer yearLevel);
    
    @Query("SELECT ss FROM SubjectSchedule ss WHERE " +
           "(SELECT COUNT(e) FROM Enrollment e WHERE e.subjectSchedule = ss AND e.status = 'ENROLLED') < ss.maxSlots")
    List<SubjectSchedule> findAvailableSchedules();
    
    // Find available schedules excluding subjects the student is already enrolled in - exact year level match
    @Query("SELECT ss FROM SubjectSchedule ss WHERE ss.subject.yearLevel = :yearLevel " +
           "AND (SELECT COUNT(e) FROM Enrollment e WHERE e.subjectSchedule = ss AND e.status = 'ENROLLED') < ss.maxSlots " +
           "AND ss.subject.id NOT IN (" +
           "SELECT e.subject.id FROM Enrollment e WHERE e.student = :student AND e.status = 'ENROLLED'" +
           ")")
    List<SubjectSchedule> findAvailableSchedulesForStudentExcludingEnrolled(@Param("yearLevel") Integer yearLevel, @Param("student") Student student);
    
    // Find available schedules for a specific semester and academic year
    @Query("SELECT ss FROM SubjectSchedule ss WHERE ss.subject.yearLevel = :yearLevel " +
           "AND ss.semester = :semester AND ss.academicYear = :academicYear " +
           "AND (SELECT COUNT(e) FROM Enrollment e WHERE e.subjectSchedule = ss AND e.status = 'ENROLLED') < ss.maxSlots " +
           "AND ss.subject.id NOT IN (" +
           "SELECT e.subject.id FROM Enrollment e WHERE e.student = :student AND e.status = 'ENROLLED'" +
           ")")
    List<SubjectSchedule> findAvailableSchedulesForStudentBySemester(@Param("yearLevel") Integer yearLevel, 
                                                                    @Param("student") Student student,
                                                                    @Param("semester") String semester,
                                                                    @Param("academicYear") String academicYear);
    
    // Find schedules by semester and academic year
    @Query("SELECT ss FROM SubjectSchedule ss WHERE ss.semester = :semester AND ss.academicYear = :academicYear")
    List<SubjectSchedule> findBySemesterAndAcademicYear(@Param("semester") String semester, @Param("academicYear") String academicYear);
    
    // Find current semester schedules
    @Query("SELECT ss FROM SubjectSchedule ss WHERE ss.semester = :semester AND ss.academicYear = :academicYear " +
           "ORDER BY ss.subject.subjectCode ASC")
    List<SubjectSchedule> findCurrentSemesterSchedules(@Param("semester") String semester, @Param("academicYear") String academicYear);
    

    
    @Query("SELECT ss FROM SubjectSchedule ss JOIN FETCH ss.subject JOIN FETCH ss.faculty WHERE ss.subject.id = :subjectId AND ss.subject.yearLevel = :yearLevel AND ss.semester = :semester AND ss.academicYear = :academicYear AND ss.currentEnrolled < ss.maxSlots")
    List<SubjectSchedule> findSchedulesForSubjectByStudentCriteria(@Param("subjectId") Long subjectId, 
                                                                  @Param("yearLevel") Integer yearLevel, 
                                                                  @Param("semester") String semester, 
                                                                  @Param("academicYear") String academicYear);
} 