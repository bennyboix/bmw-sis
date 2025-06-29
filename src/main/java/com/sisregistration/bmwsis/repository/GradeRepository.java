package com.sisregistration.bmwsis.repository;

import com.sisregistration.bmwsis.entity.Grade;
import com.sisregistration.bmwsis.entity.Student;
import com.sisregistration.bmwsis.entity.Faculty;
import com.sisregistration.bmwsis.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudent(Student student);
    List<Grade> findByStudentOrderBySemesterAscAcademicYearAsc(Student student);
    List<Grade> findByFacultyId(Long facultyId);
    
    // Enhanced queries for comprehensive grade management
    List<Grade> findByFacultyOrderByStudentLastNameAscStudentFirstNameAsc(Faculty faculty);
    
    List<Grade> findByFacultyAndSubjectOrderByStudentLastNameAsc(Faculty faculty, Subject subject);
    
    List<Grade> findByFacultyAndSectionCodeOrderByStudentLastNameAsc(Faculty faculty, String sectionCode);
    
    List<Grade> findByFacultyAndSemesterAndAcademicYearOrderBySubjectAscStudentLastNameAsc(
        Faculty faculty, String semester, String academicYear);
    
    List<Grade> findByStudentAndSubjectAndFacultyAndSectionCodeAndSemesterAndAcademicYear(
        Student student, Subject subject, Faculty faculty, String sectionCode, String semester, String academicYear);
    
    @Query("SELECT g FROM Grade g WHERE g.faculty = :faculty AND g.status = :status ORDER BY g.student.lastName ASC")
    List<Grade> findByFacultyAndStatus(@Param("faculty") Faculty faculty, @Param("status") String status);
    
    @Query("SELECT g FROM Grade g WHERE g.faculty = :faculty AND (g.midtermGrade IS NULL OR g.finalGrade IS NULL) ORDER BY g.student.lastName ASC")
    List<Grade> findByFacultyWithPendingGrades(@Param("faculty") Faculty faculty);
    
    @Query("SELECT g FROM Grade g WHERE g.student = :student AND g.semester = :semester AND g.academicYear = :academicYear ORDER BY g.subject.subjectCode ASC")
    List<Grade> findByStudentAndSemesterAndAcademicYear(@Param("student") Student student, @Param("semester") String semester, @Param("academicYear") String academicYear);
    
    @Query("SELECT COUNT(g) FROM Grade g WHERE g.faculty = :faculty AND g.status = 'PASSED'")
    Long countPassedByFaculty(@Param("faculty") Faculty faculty);
    
    @Query("SELECT COUNT(g) FROM Grade g WHERE g.faculty = :faculty AND g.status = 'FAILED'")
    Long countFailedByFaculty(@Param("faculty") Faculty faculty);
    
    @Query("SELECT COUNT(g) FROM Grade g WHERE g.faculty = :faculty AND (g.midtermGrade IS NULL OR g.finalGrade IS NULL)")
    Long countPendingByFaculty(@Param("faculty") Faculty faculty);
    
    @Query("SELECT AVG(g.finalRating) FROM Grade g WHERE g.faculty = :faculty AND g.finalRating IS NOT NULL")
    Double getAverageGradeByFaculty(@Param("faculty") Faculty faculty);

    @Query("SELECT COUNT(g) FROM Grade g WHERE g.student = :student AND g.subject = :subject AND g.faculty = :faculty AND g.sectionCode = :sectionCode AND g.semester = :semester AND g.academicYear = :academicYear")
    Long countByStudentAndSubjectAndFacultyAndSectionCodeAndSemesterAndAcademicYear(
        @Param("student") Student student, 
        @Param("subject") Subject subject, 
        @Param("faculty") Faculty faculty, 
        @Param("sectionCode") String sectionCode, 
        @Param("semester") String semester, 
        @Param("academicYear") String academicYear);

    @Query("SELECT g FROM Grade g WHERE g.student = :student AND g.subject = :subject AND g.faculty = :faculty AND g.sectionCode = :sectionCode AND g.semester = :semester AND g.academicYear = :academicYear ORDER BY g.id ASC")
    List<Grade> findByStudentAndSubjectAndFacultyAndSectionCodeAndSemesterAndAcademicYearOrderById(
        @Param("student") Student student, 
        @Param("subject") Subject subject, 
        @Param("faculty") Faculty faculty, 
        @Param("sectionCode") String sectionCode, 
        @Param("semester") String semester, 
        @Param("academicYear") String academicYear);
} 