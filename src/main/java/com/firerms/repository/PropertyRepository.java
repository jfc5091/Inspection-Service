package com.firerms.repository;

import com.firerms.entity.property.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    Property findByPropertyId(Long propertyId);
}
