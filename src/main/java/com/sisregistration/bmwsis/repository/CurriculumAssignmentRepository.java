package com.sisregistration.bmwsis.repository;

import com.sisregistration.bmwsis.entity.CurriculumAssignment;
import com.sisregistration.bmwsis.entity.Student;
import com.sisregistration.bmwsis.entity.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurriculumAssignmentRepository extends JpaRepository<CurriculumAssignment, Long> {
    
    // Find all assignments for a student
    List<CurriculumAssignment> findByStudent(Student student);
    
    // Find all active assignments for a student
    @Query("SELECT ca FROM CurriculumAssignment ca WHERE ca.student = :student AND ca.status = 'ACTIVE'")
    List<CurriculumAssignment> findActiveByStudent(@Param("student") Student student);
    
    // Find assignments by student ID
    @Query("SELECT ca FROM CurriculumAssignment ca WHERE ca.student.id = :studentId AND ca.status = 'ACTIVE'")
    List<CurriculumAssignment> findActiveByStudentId(@Param("studentId") Long studentId);
    
    // Check if a student is already assigned to a curriculum
    @Query("SELECT ca FROM CurriculumAssignment ca WHERE ca.student.id = :studentId AND ca.curriculum.id = :curriculumId AND ca.status = 'ACTIVE'")
    Optional<CurriculumAssignment> findActiveAssignment(@Param("studentId") Long studentId, @Param("curriculumId") Long curriculumId);
    
    // Find all students assigned to a curriculum
    @Query("SELECT ca FROM CurriculumAssignment ca WHERE ca.curriculum = :curriculum AND ca.status = 'ACTIVE'")
    List<CurriculumAssignment> findActiveByCurriculum(@Param("curriculum") Curriculum curriculum);
    
    // Find assignments by semester and academic year
    @Query("SELECT ca FROM CurriculumAssignment ca WHERE ca.semester = :semester AND ca.academicYear = :academicYear AND ca.status = 'ACTIVE'")
    List<CurriculumAssignment> findActiveBySemesterAndAcademicYear(@Param("semester") String semester, @Param("academicYear") String academicYear);
    
    // Find assignments for a student in a specific semester/year
    @Query("SELECT ca FROM CurriculumAssignment ca WHERE ca.student = :student AND ca.semester = :semester AND ca.academicYear = :academicYear AND ca.status = 'ACTIVE'")
    List<CurriculumAssignment> findActiveByStudentAndSemester(@Param("student") Student student, @Param("semester") String semester, @Param("academicYear") String academicYear);
    
    // Find all curriculum IDs assigned to a student
    @Query("SELECT ca.curriculum.id FROM CurriculumAssignment ca WHERE ca.student = :student AND ca.status = 'ACTIVE'")
    List<Long> findCurriculumIdsByStudent(@Param("student") Student student);
    
    // Count active assignments for a curriculum
    @Query("SELECT COUNT(ca) FROM CurriculumAssignment ca WHERE ca.curriculum.id = :curriculumId AND ca.status = 'ACTIVE'")
    Long countActiveAssignmentsByCurriculum(@Param("curriculumId") Long curriculumId);
} 