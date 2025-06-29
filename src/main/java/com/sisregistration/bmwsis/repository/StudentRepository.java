package com.sisregistration.bmwsis.repository;

import com.sisregistration.bmwsis.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentId(String studentId);
    Optional<Student> findByStudentIdAndPassword(String studentId, String password);
    boolean existsByStudentId(String studentId);
    List<Student> findByCourse(String course);
} 