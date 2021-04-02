package com.firerms.repository;

import com.firerms.entity.checklists.FireCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FireCodeRepository extends JpaRepository<FireCode, Long> {

    FireCode findByFireCodeId(Long fireCodeId);
}
