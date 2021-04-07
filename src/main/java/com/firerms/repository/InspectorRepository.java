package com.firerms.repository;

import com.firerms.entity.inspections.Inspector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectorRepository extends JpaRepository<Inspector, Long> {
    Inspector findByInspectorId(Long inspectorId);
}
