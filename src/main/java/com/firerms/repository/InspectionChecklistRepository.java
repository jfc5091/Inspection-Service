package com.firerms.repository;

import com.firerms.entity.checklists.InspectionChecklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectionChecklistRepository extends JpaRepository<InspectionChecklist, Long> {

    InspectionChecklist findByInspectionChecklistId(Long inspectionChecklistId);
}
