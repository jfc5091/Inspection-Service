package com.firerms.repository;

import com.firerms.entity.checklists.InspectionViolationImageUrl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectionViolationImageUrlRepository extends JpaRepository<InspectionViolationImageUrl, Long> {

    InspectionViolationImageUrl findByInspectionViolationImageId(Long inspectionViolationImageId);
}
