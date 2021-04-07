package com.firerms.repository;

import com.firerms.entity.checklists.InspectionViolationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectionViolationStatusRepository extends JpaRepository<InspectionViolationStatus, Long> {
    InspectionViolationStatus findByInspectionViolationStatusId(Long inspectionViolationStatusId);

}
