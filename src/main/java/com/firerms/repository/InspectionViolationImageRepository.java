package com.firerms.repository;

import com.firerms.entity.checklists.InspectionViolationImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectionViolationImageRepository extends JpaRepository<InspectionViolationImage, Long> {

    InspectionViolationImage findByInspectionViolationImageId(Long inspectionViolationImageId);
}
