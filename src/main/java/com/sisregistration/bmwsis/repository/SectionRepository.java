package com.sisregistration.bmwsis.repository;

import com.sisregistration.bmwsis.entity.Section;
import com.sisregistration.bmwsis.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    
    Optional<Section> findBySectionCode(String sectionCode);
    
    List<Section> findByCourse(String course);
    
    List<Section> findByYearLevel(Integer yearLevel);
    
    List<Section> findByCourseAndYearLevel(String course, Integer yearLevel);
    
    List<Section> findByStatus(String status);
    
    List<Section> findByAdviser(Faculty adviser);
    
    @Query("SELECT s FROM Section s WHERE s.course = :course AND s.yearLevel = :yearLevel AND s.academicYear = :academicYear")
    List<Section> findByCourseAndYearLevelAndAcademicYear(@Param("course") String course, 
                                                         @Param("yearLevel") Integer yearLevel, 
                                                         @Param("academicYear") String academicYear);
    
    @Query("SELECT s FROM Section s WHERE s.academicYear = :academicYear AND s.status = 'ACTIVE'")
    List<Section> findActiveByAcademicYear(@Param("academicYear") String academicYear);
    
    @Query("SELECT s FROM Section s WHERE s.currentEnrollment < s.maxCapacity AND s.status = 'ACTIVE'")
    List<Section> findAvailableSections();
} 