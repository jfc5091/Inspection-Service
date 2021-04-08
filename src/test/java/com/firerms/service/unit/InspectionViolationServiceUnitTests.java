package com.firerms.service.unit;

import com.firerms.entity.checklists.FireCode;
import com.firerms.entity.checklists.InspectionChecklistItem;
import com.firerms.entity.checklists.InspectionViolation;
import com.firerms.entity.checklists.InspectionViolationStatus;
import com.firerms.entity.inspections.Inspection;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.IdNotNullException;
import com.firerms.multiTenancy.TenantContext;
import com.firerms.repository.*;
import com.firerms.request.InspectionViolationRequest;
import com.firerms.response.InspectionViolationResponse;
import com.firerms.service.InspectionViolationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("local")
@SpringBootTest
public class InspectionViolationServiceUnitTests {

    @Autowired
    private InspectionViolationService inspectionViolationService;

    @MockBean
    private InspectionViolationRepository inspectionViolationRepository;

    @MockBean
    private FireCodeRepository fireCodeRepository;

    @MockBean
    private InspectionViolationStatusRepository inspectionViolationStatusRepository;

    @MockBean
    private InspectionRepository inspectionRepository;

    @MockBean
    private InspectionChecklistItemRepository inspectionChecklistItemRepository;

    private final Long testFdid = 1L;

    @BeforeEach
    void setCurrentTenant() {
        TenantContext.setCurrentTenant(testFdid.toString());
    }

    @Transactional
    @Test
    void createInspectionViolationTest() throws Exception {
        InspectionViolation inspectionViolation = new InspectionViolation(null, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        when(fireCodeRepository.findByFireCodeId(inspectionViolation.getFireCodeId())).thenReturn(new FireCode());
        when(inspectionViolationStatusRepository.findByInspectionViolationStatusId(inspectionViolation.getInspectionViolationStatusId())).thenReturn(new InspectionViolationStatus());
        when(inspectionRepository.findByInspectionId(inspectionViolation.getInspectionId())).thenReturn(new Inspection());
        when(inspectionChecklistItemRepository.findByInspectionChecklistItemId(inspectionViolation.getInspectionChecklistItemId())).thenReturn(new InspectionChecklistItem());
        when(inspectionViolationRepository.save(inspectionViolation)).thenReturn(inspectionViolation);
        InspectionViolationRequest inspectionViolationRequest = new InspectionViolationRequest(inspectionViolation);

        InspectionViolationResponse savedInspectionViolationResponse = inspectionViolationService.createInspectionViolation(inspectionViolationRequest);
        InspectionViolation savedInspectionViolation = savedInspectionViolationResponse.getInspectionViolation();

        assertNotNull(savedInspectionViolation);
        assertEquals(inspectionViolation.getFireCodeId(), savedInspectionViolation.getFireCodeId());
        assertEquals(inspectionViolation.getInspectionViolationStatusId(), savedInspectionViolation.getInspectionViolationStatusId());
        assertEquals(inspectionViolation.getInspectionId(), savedInspectionViolation.getInspectionId());
        assertEquals(inspectionViolation.getInspectionChecklistItemId(), savedInspectionViolation.getInspectionChecklistItemId());
        assertEquals(inspectionViolation.getDescription(), savedInspectionViolation.getDescription());
        assertEquals(inspectionViolation.getLocation(), savedInspectionViolation.getLocation());
        assertEquals(inspectionViolation.getNarrative(), savedInspectionViolation.getNarrative());
        assertEquals(inspectionViolation.getDateFound(), savedInspectionViolation.getDateFound());
        assertEquals(inspectionViolation.getAbateDate(), savedInspectionViolation.getAbateDate());
        assertEquals(inspectionViolation.getDateCorrected(), savedInspectionViolation.getDateCorrected());
        assertEquals(inspectionViolation.getFdid(), savedInspectionViolation.getFdid());
    }

    @Transactional
    @Test
    void createInspectionViolationWithNonNullIdTest() {
        InspectionViolation inspectionViolation = new InspectionViolation(1L, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        InspectionViolationRequest inspectionViolationRequest = new InspectionViolationRequest(inspectionViolation);

        IdNotNullException exception = assertThrows(
                IdNotNullException.class,
                () -> inspectionViolationService.createInspectionViolation(inspectionViolationRequest)
        );

        assertEquals("id must be null for new Inspection Violation", exception.getMessage());
    }

    @Transactional
    @Test
    void createInspectionViolationFireCodeIdDoesNotExistTest() {
        InspectionViolation inspectionViolation = new InspectionViolation(null, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        InspectionViolationRequest inspectionViolationRequest = new InspectionViolationRequest(inspectionViolation);
        when(fireCodeRepository.findByFireCodeId(inspectionViolation.getFireCodeId())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionViolationService.createInspectionViolation(inspectionViolationRequest)
        );

        assertEquals("Fire code not found with id: " + inspectionViolation.getFireCodeId(), exception.getMessage());
    }

    @Transactional
    @Test
    void createInspectionViolationInspectionViolationStatusIdDoesNotExistTest() {
        InspectionViolation inspectionViolation = new InspectionViolation(null, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        InspectionViolationRequest inspectionViolationRequest = new InspectionViolationRequest(inspectionViolation);
        when(fireCodeRepository.findByFireCodeId(inspectionViolation.getFireCodeId())).thenReturn(new FireCode());
        when(inspectionViolationStatusRepository.findByInspectionViolationStatusId(inspectionViolation.getInspectionViolationStatusId())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionViolationService.createInspectionViolation(inspectionViolationRequest)
        );

        assertEquals("Inspection Violation Status not found with id: " + inspectionViolation.getInspectionViolationStatusId(), exception.getMessage());
    }

    @Transactional
    @Test
    void createInspectionViolationInspectionIdDoesNotExistTest() {
        InspectionViolation inspectionViolation = new InspectionViolation(null, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        InspectionViolationRequest inspectionViolationRequest = new InspectionViolationRequest(inspectionViolation);
        when(fireCodeRepository.findByFireCodeId(inspectionViolation.getFireCodeId())).thenReturn(new FireCode());
        when(inspectionViolationStatusRepository.findByInspectionViolationStatusId(inspectionViolation.getInspectionViolationStatusId())).thenReturn(new InspectionViolationStatus());
        when(inspectionRepository.findByInspectionId(inspectionViolation.getInspectionId())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionViolationService.createInspectionViolation(inspectionViolationRequest)
        );

        assertEquals("Inspection not found with id: " + inspectionViolation.getInspectionId(), exception.getMessage());
    }

    @Transactional
    @Test
    void createInspectionViolationInspectionChecklistItemIdDoesNotExistTest() {
        InspectionViolation inspectionViolation = new InspectionViolation(null, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        InspectionViolationRequest inspectionViolationRequest = new InspectionViolationRequest(inspectionViolation);
        when(fireCodeRepository.findByFireCodeId(inspectionViolation.getFireCodeId())).thenReturn(new FireCode());
        when(inspectionViolationStatusRepository.findByInspectionViolationStatusId(inspectionViolation.getInspectionViolationStatusId())).thenReturn(new InspectionViolationStatus());
        when(inspectionRepository.findByInspectionId(inspectionViolation.getInspectionId())).thenReturn(new Inspection());
        when(inspectionChecklistItemRepository.findByInspectionChecklistItemId(inspectionViolation.getInspectionChecklistItemId())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionViolationService.createInspectionViolation(inspectionViolationRequest)
        );

        assertEquals("Inspection Checklist Item not found with id: " + inspectionViolation.getInspectionChecklistItemId(), exception.getMessage());
    }

    @Transactional
    @Test
    void findInspectionViolationByIdTest() throws Exception {
        InspectionViolation inspectionViolation = new InspectionViolation(1L, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        when(inspectionViolationRepository.findByInspectionViolationId(1L)).thenReturn(inspectionViolation);

        InspectionViolationResponse inspectionViolationResponse = inspectionViolationService.findInspectionViolationById(1L);
        InspectionViolation foundInspectionViolation = inspectionViolationResponse.getInspectionViolation();

        assertNotNull(foundInspectionViolation);
        assertEquals(inspectionViolation.getFireCodeId(), foundInspectionViolation.getFireCodeId());
        assertEquals(inspectionViolation.getInspectionViolationStatusId(), foundInspectionViolation.getInspectionViolationStatusId());
        assertEquals(inspectionViolation.getInspectionId(), foundInspectionViolation.getInspectionId());
        assertEquals(inspectionViolation.getInspectionChecklistItemId(), foundInspectionViolation.getInspectionChecklistItemId());
        assertEquals(inspectionViolation.getDescription(), foundInspectionViolation.getDescription());
        assertEquals(inspectionViolation.getLocation(), foundInspectionViolation.getLocation());
        assertEquals(inspectionViolation.getNarrative(), foundInspectionViolation.getNarrative());
        assertEquals(inspectionViolation.getDateFound(), foundInspectionViolation.getDateFound());
        assertEquals(inspectionViolation.getAbateDate(), foundInspectionViolation.getAbateDate());
        assertEquals(inspectionViolation.getDateCorrected(), foundInspectionViolation.getDateCorrected());
        assertEquals(inspectionViolation.getFdid(), foundInspectionViolation.getFdid());
    }

    @Transactional
    @Test
    void findInspectionViolationByIdDoesNotExistTest() {
        Long inspectionViolationId = 1L;

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionViolationService.findInspectionViolationById(inspectionViolationId)
        );

        assertEquals("Inspection Violation not found with id: " + inspectionViolationId, exception.getMessage());
    }

    @Transactional
    @Test
    void updateInspectionViolationTest() throws Exception {
        InspectionViolation originalInspectionViolation = new InspectionViolation(1L, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        InspectionViolation updateInspectionViolation = new InspectionViolation(1L, 1L, 1L,
                1L, 1L, "new description", "new location", "new narrative",
                new Date(), new Date(), new Date(), testFdid);
        when(inspectionViolationRepository.findByInspectionViolationId(updateInspectionViolation.getInspectionViolationId())).thenReturn(originalInspectionViolation);
        when(fireCodeRepository.findByFireCodeId(updateInspectionViolation.getFireCodeId())).thenReturn(new FireCode());
        when(inspectionViolationStatusRepository.findByInspectionViolationStatusId(updateInspectionViolation.getInspectionViolationStatusId())).thenReturn(new InspectionViolationStatus());
        when(inspectionRepository.findByInspectionId(updateInspectionViolation.getInspectionId())).thenReturn(new Inspection());
        when(inspectionChecklistItemRepository.findByInspectionChecklistItemId(updateInspectionViolation.getInspectionChecklistItemId())).thenReturn(new InspectionChecklistItem());
        when(inspectionViolationRepository.save(updateInspectionViolation)).thenReturn(updateInspectionViolation);
        InspectionViolationRequest updatedInspectionViolationRequest = new InspectionViolationRequest(updateInspectionViolation);

        InspectionViolationResponse updatedInspectionViolationResponse = inspectionViolationService.updateInspectionViolation(updatedInspectionViolationRequest);
        InspectionViolation updatedInspectionViolation = updatedInspectionViolationResponse.getInspectionViolation();

        assertNotNull(updatedInspectionViolation);
        assertEquals(updateInspectionViolation.getFireCodeId(), updatedInspectionViolation.getFireCodeId());
        assertEquals(updateInspectionViolation.getInspectionViolationStatusId(), updatedInspectionViolation.getInspectionViolationStatusId());
        assertEquals(updateInspectionViolation.getInspectionId(), updatedInspectionViolation.getInspectionId());
        assertEquals(updateInspectionViolation.getInspectionChecklistItemId(), updatedInspectionViolation.getInspectionChecklistItemId());
        assertEquals(updateInspectionViolation.getDescription(), updatedInspectionViolation.getDescription());
        assertEquals(updateInspectionViolation.getLocation(), updatedInspectionViolation.getLocation());
        assertEquals(updateInspectionViolation.getNarrative(), updatedInspectionViolation.getNarrative());
        assertEquals(updateInspectionViolation.getDateFound(), updatedInspectionViolation.getDateFound());
        assertEquals(updateInspectionViolation.getAbateDate(), updatedInspectionViolation.getAbateDate());
        assertEquals(updateInspectionViolation.getDateCorrected(), updatedInspectionViolation.getDateCorrected());
        assertEquals(updateInspectionViolation.getFdid(), updatedInspectionViolation.getFdid());
    }

    @Transactional
    @Test
    void updateInspectionViolationDoesNotExistTest() {
        InspectionViolation inspectionViolation = new InspectionViolation(1L, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        when(inspectionViolationRepository.findByInspectionViolationId(inspectionViolation.getInspectionViolationId())).thenReturn(null);
        InspectionViolationRequest inspectionViolationRequest = new InspectionViolationRequest(inspectionViolation);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionViolationService.updateInspectionViolation(inspectionViolationRequest)
        );

        assertEquals("Inspection Violation not found with id: " + inspectionViolation.getInspectionViolationId(), exception.getMessage());
    }

    @Transactional
    @Test
    void updateInspectionViolationFireCodeIdDoesNotExistTest() {
        InspectionViolation inspectionViolation = new InspectionViolation(null, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        InspectionViolationRequest inspectionViolationRequest = new InspectionViolationRequest(inspectionViolation);
        when(inspectionViolationRepository.findByInspectionViolationId(inspectionViolation.getInspectionViolationId())).thenReturn(inspectionViolation);
        when(fireCodeRepository.findByFireCodeId(inspectionViolation.getFireCodeId())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionViolationService.updateInspectionViolation(inspectionViolationRequest)
        );

        assertEquals("Fire code not found with id: " + inspectionViolation.getFireCodeId(), exception.getMessage());
    }

    @Transactional
    @Test
    void updateInspectionViolationInspectionViolationStatusIdDoesNotExistTest() {
        InspectionViolation inspectionViolation = new InspectionViolation(null, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        InspectionViolationRequest inspectionViolationRequest = new InspectionViolationRequest(inspectionViolation);
        when(inspectionViolationRepository.findByInspectionViolationId(inspectionViolation.getInspectionViolationId())).thenReturn(inspectionViolation);
        when(fireCodeRepository.findByFireCodeId(inspectionViolation.getFireCodeId())).thenReturn(new FireCode());
        when(inspectionViolationStatusRepository.findByInspectionViolationStatusId(inspectionViolation.getInspectionViolationStatusId())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionViolationService.updateInspectionViolation(inspectionViolationRequest)
        );

        assertEquals("Inspection Violation Status not found with id: " + inspectionViolation.getInspectionViolationStatusId(), exception.getMessage());
    }

    @Transactional
    @Test
    void updateInspectionViolationInspectionIdDoesNotExistTest() {
        InspectionViolation inspectionViolation = new InspectionViolation(null, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        InspectionViolationRequest inspectionViolationRequest = new InspectionViolationRequest(inspectionViolation);
        when(inspectionViolationRepository.findByInspectionViolationId(inspectionViolation.getInspectionViolationId())).thenReturn(inspectionViolation);
        when(fireCodeRepository.findByFireCodeId(inspectionViolation.getFireCodeId())).thenReturn(new FireCode());
        when(inspectionViolationStatusRepository.findByInspectionViolationStatusId(inspectionViolation.getInspectionViolationStatusId())).thenReturn(new InspectionViolationStatus());
        when(inspectionRepository.findByInspectionId(inspectionViolation.getInspectionId())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionViolationService.updateInspectionViolation(inspectionViolationRequest)
        );

        assertEquals("Inspection not found with id: " + inspectionViolation.getInspectionId(), exception.getMessage());
    }

    @Transactional
    @Test
    void updateInspectionViolationInspectionChecklistItemIdDoesNotExistTest() {
        InspectionViolation inspectionViolation = new InspectionViolation(null, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        InspectionViolationRequest inspectionViolationRequest = new InspectionViolationRequest(inspectionViolation);
        when(inspectionViolationRepository.findByInspectionViolationId(inspectionViolation.getInspectionViolationId())).thenReturn(inspectionViolation);
        when(fireCodeRepository.findByFireCodeId(inspectionViolation.getFireCodeId())).thenReturn(new FireCode());
        when(inspectionViolationStatusRepository.findByInspectionViolationStatusId(inspectionViolation.getInspectionViolationStatusId())).thenReturn(new InspectionViolationStatus());
        when(inspectionRepository.findByInspectionId(inspectionViolation.getInspectionId())).thenReturn(new Inspection());
        when(inspectionChecklistItemRepository.findByInspectionChecklistItemId(inspectionViolation.getInspectionChecklistItemId())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionViolationService.updateInspectionViolation(inspectionViolationRequest)
        );

        assertEquals("Inspection Checklist Item not found with id: " + inspectionViolation.getInspectionChecklistItemId(), exception.getMessage());
    }

    @Transactional
    @Test
    void deleteInspectionViolationTest() {
        InspectionViolation inspectionViolation = new InspectionViolation(1L, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        when(inspectionViolationRepository.findByInspectionViolationId(inspectionViolation.getInspectionViolationId())).thenReturn(inspectionViolation);

        inspectionViolationService.deleteInspectionViolation(inspectionViolation.getInspectionViolationId());

        verify(inspectionViolationRepository, times(1)).delete(inspectionViolation);
    }

    @Transactional
    @Test
    void deleteInspectionViolationDoesNotExistTest() {
        Long inspectionViolationId = 1L;
        when(inspectionViolationRepository.findByInspectionViolationId(inspectionViolationId)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionViolationService.deleteInspectionViolation(inspectionViolationId)
        );

        assertEquals("Inspection Violation not found with id: " + inspectionViolationId, exception.getMessage());
    }
}
