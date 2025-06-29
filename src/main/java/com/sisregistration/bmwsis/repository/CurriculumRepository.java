package com.sisregistration.bmwsis.repository;

import com.sisregistration.bmwsis.entity.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {
    
    Optional<Curriculum> findByCurriculumCode(String curriculumCode);
    
    List<Curriculum> findByCourse(String course);
    
    List<Curriculum> findByYearLevel(Integer yearLevel);
    
    List<Curriculum> findByCourseAndYearLevel(String course, Integer yearLevel);
    
    List<Curriculum> findByStatus(String status);
    
    @Query("SELECT c FROM Curriculum c WHERE c.course = :course AND c.yearLevel = :yearLevel AND c.semester = :semester")
    List<Curriculum> findByCourseAndYearLevelAndSemester(@Param("course") String course, 
                                                        @Param("yearLevel") Integer yearLevel, 
                                                        @Param("semester") Integer semester);
    
    @Query("SELECT c FROM Curriculum c WHERE c.academicYear = :academicYear AND c.status = 'ACTIVE'")
    List<Curriculum> findActiveByAcademicYear(@Param("academicYear") String academicYear);
    
    @Query("SELECT c FROM Curriculum c LEFT JOIN FETCH c.subjects WHERE c.course = :course AND c.yearLevel = :yearLevel AND c.semester = :semester AND c.academicYear = :academicYear AND c.status = 'ACTIVE'")
    List<Curriculum> findActiveCurriculumsByStudentCriteria(@Param("course") String course, 
                                                           @Param("yearLevel") Integer yearLevel, 
                                                           @Param("semester") Integer semester, 
                                                           @Param("academicYear") String academicYear);
    
    @Query("SELECT c FROM Curriculum c JOIN c.subjects s WHERE s.id = :subjectId")
    List<Curriculum> findCurriculumsBySubjectId(@Param("subjectId") Long subjectId);
} 