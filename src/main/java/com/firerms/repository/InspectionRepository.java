package com.firerms.repository;

import com.firerms.entity.inspections.Inspection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectionRepository extends JpaRepository<Inspection, Long> {

    Inspection findByInspectionId(Long inspectionId);
}
