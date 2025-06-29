package com.sisregistration.bmwsis.repository;

import com.sisregistration.bmwsis.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {
    Optional<Program> findByProgramCode(String programCode);
    List<Program> findByDepartment(String department);
    List<Program> findByStatus(String status);
    List<Program> findByDegreeLevel(String degreeLevel);
    List<Program> findByStatusAndDepartment(String status, String department);
    boolean existsByProgramCode(String programCode);
    List<Program> findByStatusOrderByProgramCodeAsc(String status);
    List<Program> findAllByOrderByProgramCodeAsc();
} 