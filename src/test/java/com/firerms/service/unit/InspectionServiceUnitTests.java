package com.firerms.service.unit;

import com.firerms.entity.checklists.InspectionChecklist;
import com.firerms.entity.inspections.Inspection;
import com.firerms.entity.inspections.Inspector;
import com.firerms.entity.property.Property;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.IdNotNullException;
import com.firerms.multiTenancy.TenantContext;
import com.firerms.repository.InspectionChecklistRepository;
import com.firerms.repository.InspectionRepository;
import com.firerms.repository.InspectorRepository;
import com.firerms.repository.PropertyRepository;
import com.firerms.request.InspectionRequest;
import com.firerms.response.InspectionResponse;
import com.firerms.service.InspectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("local")
@SpringBootTest
public class InspectionServiceUnitTests {

    @Autowired
    private InspectionService inspectionService;

    @MockBean
    private InspectionRepository inspectionRepository;

    @MockBean
    private PropertyRepository propertyRepository;

    @MockBean
    private InspectionChecklistRepository inspectionChecklistRepository;

    @MockBean
    private InspectorRepository inspectorRepository;

    private final Long testFdid = 1L;

    @BeforeEach
    void setCurrentTenant() {
        TenantContext.setCurrentTenant(testFdid.toString());
    }

    @Transactional
    @Test
    void createInspectionTest() throws Exception {
        Inspection inspection = new Inspection(null, 1L, 1L, 1L, "status",
                "narrative", "occupantSignatureUrl", "inspectorSignatureUrl", testFdid);
        when(inspectionRepository.save(inspection)).thenReturn(inspection);
        when(propertyRepository.findByPropertyId(inspection.getPropertyId())).thenReturn(new Property());
        when(inspectionChecklistRepository.findByInspectionChecklistId(inspection.getInspectionChecklistId())).thenReturn(new InspectionChecklist());
        when(inspectorRepository.findByInspectorId(inspection.getInspectorId())).thenReturn(new Inspector());
        InspectionRequest inspectionRequest = new InspectionRequest(inspection);

        InspectionResponse savedInspectionResponse = inspectionService.createInspection(inspectionRequest);
        Inspection savedInspection = savedInspectionResponse.getInspection();

        assertNotNull(savedInspection);
        assertEquals(inspection.getPropertyId(), savedInspection.getPropertyId());
        assertEquals(inspection.getInspectorId(), savedInspection.getInspectorId());
        assertEquals(inspection.getInspectionChecklistId(), savedInspection.getInspectionChecklistId());
        assertEquals(inspection.getStatus(), savedInspection.getStatus());
        assertEquals(inspection.getNarrative(), savedInspection.getNarrative());
        assertEquals(inspection.getInspectorSignatureUrl(), savedInspection.getInspectorSignatureUrl());
        assertEquals(inspection.getInspectorSignatureUrl(), savedInspection.getInspectorSignatureUrl());
        assertEquals(inspection.getFdid(), savedInspection.getFdid());
    }

    @Transactional
    @Test
    void createInspectionWithNonNullIdTest() {
        Inspection inspection = new Inspection(1L, 1L, 1L, 1L, "status",
                "narrative", "occupantSignatureUrl", "inspectorSignatureUrl", testFdid);
        InspectionRequest inspectionRequest = new InspectionRequest(inspection);

        IdNotNullException exception = assertThrows(
                IdNotNullException.class,
                () -> inspectionService.createInspection(inspectionRequest)
        );

        assertEquals("id must be null for new Inspection", exception.getMessage());
    }

    @Transactional
    @Test
    void createInspectionPropertyIdDoesNotExistTest() {
        Inspection inspection = new Inspection(null, 1L, 1L, 1L, "status",
                "narrative", "occupantSignatureUrl", "inspectorSignatureUrl", testFdid);
        InspectionRequest inspectionRequest = new InspectionRequest(inspection);
        when(propertyRepository.findByPropertyId(inspection.getPropertyId())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionService.createInspection(inspectionRequest)
        );

        assertEquals("Property not found with id: " + inspection.getPropertyId(), exception.getMessage());
    }

    @Transactional
    @Test
    void createInspectionChecklistIdDoesNotExistTest() {
        Inspection inspection = new Inspection(null, 1L, 1L, 1L, "status",
                "narrative", "occupantSignatureUrl", "inspectorSignatureUrl", testFdid);
        InspectionRequest inspectionRequest = new InspectionRequest(inspection);
        when(propertyRepository.findByPropertyId(inspection.getPropertyId())).thenReturn(new Property());
        when(inspectionChecklistRepository.findByInspectionChecklistId(inspection.getInspectionChecklistId())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionService.createInspection(inspectionRequest)
        );

        assertEquals("Inspection Checklist not found with id: " + inspection.getPropertyId(), exception.getMessage());
    }

    @Transactional
    @Test
    void createInspectionInspectorIdDoesNotExistTest() {
        Inspection inspection = new Inspection(null, 1L, 1L, 1L, "status",
                "narrative", "occupantSignatureUrl", "inspectorSignatureUrl", testFdid);
        InspectionRequest inspectionRequest = new InspectionRequest(inspection);
        when(propertyRepository.findByPropertyId(inspection.getPropertyId())).thenReturn(new Property());
        when(inspectionChecklistRepository.findByInspectionChecklistId(inspection.getInspectionChecklistId())).thenReturn(new InspectionChecklist());
        when(inspectorRepository.findByInspectorId(inspection.getInspectorId())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionService.createInspection(inspectionRequest)
        );

        assertEquals("Inspector not found with id: " + inspection.getPropertyId(), exception.getMessage());
    }

    @Transactional
    @Test
    void findInspectionByIdTest() throws Exception {
        Inspection inspection = new Inspection(1L, 1L, 1L, 1L, "status",
                "narrative", "occupantSignatureUrl", "inspectorSignatureUrl", testFdid);
        when(inspectionRepository.findByInspectionId(1L)).thenReturn(inspection);

        InspectionResponse inspectionResponse = inspectionService.findInspectionById(1L);
        Inspection foundInspection = inspectionResponse.getInspection();

        assertNotNull(foundInspection);
        assertEquals(inspection.getPropertyId(), foundInspection.getPropertyId());
        assertEquals(inspection.getInspectorId(), foundInspection.getInspectorId());
        assertEquals(inspection.getInspectionChecklistId(), foundInspection.getInspectionChecklistId());
        assertEquals(inspection.getStatus(), foundInspection.getStatus());
        assertEquals(inspection.getNarrative(), foundInspection.getNarrative());
        assertEquals(inspection.getInspectorSignatureUrl(), foundInspection.getInspectorSignatureUrl());
        assertEquals(inspection.getInspectorSignatureUrl(), foundInspection.getInspectorSignatureUrl());
        assertEquals(inspection.getFdid(), foundInspection.getFdid());
    }

    @Transactional
    @Test
    void findInspectionByIdDoesNotExistTest() {
        Long inspectionId = 1L;

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionService.findInspectionById(inspectionId)
        );

        assertEquals("Inspection not found with id: " + inspectionId, exception.getMessage());
    }

    @Transactional
    @Test
    void updateInspectionTest() throws Exception {
        Inspection originalInspection = new Inspection(1L, 1L, 1L, 1L, "status",
                "narrative", "occupantSignatureUrl", "inspectorSignatureUrl", testFdid);
        Inspection updateInspection = new Inspection(1L, 1L, 1L, 1L, "new status",
                "new narrative", "occupantSignatureUrl", "inspectorSignatureUrl", testFdid);
        when(inspectionRepository.findByInspectionId(updateInspection.getInspectionId())).thenReturn(originalInspection);
        when(propertyRepository.findByPropertyId(updateInspection.getPropertyId())).thenReturn(new Property());
        when(inspectionChecklistRepository.findByInspectionChecklistId(updateInspection.getInspectionChecklistId())).thenReturn(new InspectionChecklist());
        when(inspectorRepository.findByInspectorId(updateInspection.getInspectorId())).thenReturn(new Inspector());
        when(inspectionRepository.save(updateInspection)).thenReturn(updateInspection);
        InspectionRequest updatedInspectionRequest = new InspectionRequest(updateInspection);

        InspectionResponse updatedInspectionResponse = inspectionService.updateInspection(updatedInspectionRequest);
        Inspection updatedInspection = updatedInspectionResponse.getInspection();

        assertNotNull(updatedInspection);
        assertEquals(updateInspection.getPropertyId(), updatedInspection.getPropertyId());
        assertEquals(updateInspection.getInspectorId(), updatedInspection.getInspectorId());
        assertEquals(updateInspection.getInspectionChecklistId(), updatedInspection.getInspectionChecklistId());
        assertEquals(updateInspection.getStatus(), updatedInspection.getStatus());
        assertEquals(updateInspection.getNarrative(), updatedInspection.getNarrative());
        assertEquals(updateInspection.getInspectorSignatureUrl(), updatedInspection.getInspectorSignatureUrl());
        assertEquals(updateInspection.getInspectorSignatureUrl(), updatedInspection.getInspectorSignatureUrl());
        assertEquals(updateInspection.getFdid(), updatedInspection.getFdid());
    }

    @Transactional
    @Test
    void updateInspectionDoesNotExistTest() {
        Inspection inspection = new Inspection(1L, 1L, 1L, 1L, "status",
                "narrative", "occupantSignatureUrl", "inspectorSignatureUrl", testFdid);
        when(inspectionRepository.findByInspectionId(inspection.getInspectionId())).thenReturn(null);
        InspectionRequest inspectionRequest = new InspectionRequest(inspection);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionService.updateInspection(inspectionRequest)
        );

        assertEquals("Inspection not found with id: " + inspection.getInspectionId(), exception.getMessage());
    }

    @Transactional
    @Test
    void updateInspectionPropertyIdDoesNotExistTest() {
        Inspection inspection = new Inspection(1L, 1L, 1L, 1L, "status",
                "narrative", "occupantSignatureUrl", "inspectorSignatureUrl", testFdid);
        InspectionRequest inspectionRequest = new InspectionRequest(inspection);
        when(inspectionRepository.findByInspectionId(inspection.getInspectionId())).thenReturn(inspection);
        when(propertyRepository.findByPropertyId(inspection.getPropertyId())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionService.updateInspection(inspectionRequest)
        );

        assertEquals("Property not found with id: " + inspection.getPropertyId(), exception.getMessage());
    }

    @Transactional
    @Test
    void updateInspectionChecklistIdDoesNotExistTest() {
        Inspection inspection = new Inspection(1L, 1L, 1L, 1L, "status",
                "narrative", "occupantSignatureUrl", "inspectorSignatureUrl", testFdid);
        InspectionRequest inspectionRequest = new InspectionRequest(inspection);
        when(inspectionRepository.findByInspectionId(inspection.getInspectionId())).thenReturn(inspection);
        when(propertyRepository.findByPropertyId(inspection.getPropertyId())).thenReturn(new Property());
        when(inspectionChecklistRepository.findByInspectionChecklistId(inspection.getInspectionChecklistId())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionService.updateInspection(inspectionRequest)
        );

        assertEquals("Inspection Checklist not found with id: " + inspection.getPropertyId(), exception.getMessage());
    }

    @Transactional
    @Test
    void updateInspectionInspectorIdDoesNotExistTest() {
        Inspection inspection = new Inspection(1L, 1L, 1L, 1L, "status",
                "narrative", "occupantSignatureUrl", "inspectorSignatureUrl", testFdid);
        InspectionRequest inspectionRequest = new InspectionRequest(inspection);
        when(inspectionRepository.findByInspectionId(inspection.getInspectionId())).thenReturn(inspection);
        when(propertyRepository.findByPropertyId(inspection.getPropertyId())).thenReturn(new Property());
        when(inspectionChecklistRepository.findByInspectionChecklistId(inspection.getInspectionChecklistId())).thenReturn(new InspectionChecklist());
        when(inspectorRepository.findByInspectorId(inspection.getInspectorId())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionService.updateInspection(inspectionRequest)
        );

        assertEquals("Inspector not found with id: " + inspection.getPropertyId(), exception.getMessage());
    }

    @Transactional
    @Test
    void deleteInspectionTest() {
        Inspection inspection = new Inspection(1L, 1L, 1L, 1L, "status",
                "narrative", "occupantSignatureUrl", "inspectorSignatureUrl", testFdid);
        when(inspectionRepository.findByInspectionId(inspection.getInspectionId())).thenReturn(inspection);

        inspectionService.deleteInspection(inspection.getInspectionId());

        verify(inspectionRepository, times(1)).delete(inspection);
    }

    @Transactional
    @Test
    void deleteInspectionDoesNotExistTest() {
        Long inspectionId = 1L;
        when(inspectionRepository.findByInspectionId(inspectionId)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionService.deleteInspection(inspectionId)
        );

        assertEquals("Inspection not found with id: " + inspectionId, exception.getMessage());
    }
}
