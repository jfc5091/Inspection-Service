package com.firerms.repository;

import com.firerms.entity.inspections.InspectionImageUrl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectionImageUrlRepository extends JpaRepository<InspectionImageUrl, Long> {

    InspectionImageUrl findByInspectionImageId(Long inspectionImageId);

    InspectionImageUrl findByImageUrl(String imageUrl);
}
