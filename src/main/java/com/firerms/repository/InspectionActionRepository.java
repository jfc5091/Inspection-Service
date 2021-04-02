package com.firerms.repository;

import com.firerms.entity.inspections.InspectionAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectionActionRepository extends JpaRepository<InspectionAction, Long> {

    InspectionAction findByInspectionActionId(Long inspectionActionId);
}
