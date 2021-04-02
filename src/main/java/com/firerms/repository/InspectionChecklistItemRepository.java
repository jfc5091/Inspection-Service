package com.firerms.repository;

import com.firerms.entity.checklists.InspectionChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectionChecklistItemRepository extends JpaRepository<InspectionChecklistItem, Long> {

    InspectionChecklistItem findByInspectionChecklistItemId(Long inspectionChecklistItemId);
}
