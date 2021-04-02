package com.firerms.repository;

import com.firerms.entity.checklists.InspectionViolation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectionViolationRepository extends JpaRepository<InspectionViolation, Long> {

    InspectionViolation findByInspectionViolationId(Long inspectionViolationId);
}
