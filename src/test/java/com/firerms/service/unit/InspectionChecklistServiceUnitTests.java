package com.firerms.service.unit;

import com.firerms.entity.checklists.InspectionChecklist;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.IdNotNullException;
import com.firerms.multiTenancy.TenantContext;
import com.firerms.repository.InspectionChecklistRepository;
import com.firerms.request.InspectionChecklistRequest;
import com.firerms.response.InspectionChecklistResponse;
import com.firerms.service.InspectionChecklistService;
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
public class InspectionChecklistServiceUnitTests {

    @Autowired
    private InspectionChecklistService inspectionChecklistService;

    @MockBean
    private InspectionChecklistRepository inspectionChecklistRepository;

    private final Long testFdid = 1L;

    @BeforeEach
    void setCurrentTenant() {
        TenantContext.setCurrentTenant(testFdid.toString());
    }

    @Transactional
    @Test
    void createInspectionChecklistTest() throws Exception {
        InspectionChecklist inspectionChecklist = new InspectionChecklist(null, "type", true, testFdid);
        when(inspectionChecklistRepository.save(inspectionChecklist)).thenReturn(inspectionChecklist);
        InspectionChecklistRequest inspectionChecklistRequest = new InspectionChecklistRequest(inspectionChecklist);

        InspectionChecklistResponse savedInspectionChecklistResponse = inspectionChecklistService.createInspectionChecklist(inspectionChecklistRequest);
        InspectionChecklist savedInspectionChecklist = savedInspectionChecklistResponse.getInspectionChecklist();

        assertNotNull(savedInspectionChecklist);
        assertEquals(inspectionChecklist.getType(), savedInspectionChecklist.getType());
        assertEquals(inspectionChecklist.isEnabled(), savedInspectionChecklist.isEnabled());
        assertEquals(inspectionChecklist.getFdid(), savedInspectionChecklist.getFdid());
    }

    @Transactional
    @Test
    void createInspectionChecklistWithNonNullIdTest() {
        InspectionChecklist inspectionChecklist = new InspectionChecklist(1L, "type", true, testFdid);
        InspectionChecklistRequest inspectionChecklistRequest = new InspectionChecklistRequest(inspectionChecklist);

        IdNotNullException exception = assertThrows(
                IdNotNullException.class,
                () -> inspectionChecklistService.createInspectionChecklist(inspectionChecklistRequest)
        );

        assertEquals("id must be null for new Inspection Checklist", exception.getMessage());
    }

    @Transactional
    @Test
    void findInspectionChecklistByIdTest() throws Exception {
        InspectionChecklist inspectionChecklist = new InspectionChecklist(1L, "type", true, testFdid);
        when(inspectionChecklistRepository.findByInspectionChecklistId(1L)).thenReturn(inspectionChecklist);

        InspectionChecklistResponse inspectionChecklistResponse = inspectionChecklistService.findInspectionChecklistById(1L);
        InspectionChecklist foundInspectionChecklist = inspectionChecklistResponse.getInspectionChecklist();

        assertNotNull(foundInspectionChecklist);
        assertEquals(inspectionChecklist.getType(), foundInspectionChecklist.getType());
        assertEquals(inspectionChecklist.isEnabled(), foundInspectionChecklist.isEnabled());
        assertEquals(inspectionChecklist.getFdid(), foundInspectionChecklist.getFdid());
    }

    @Transactional
    @Test
    void findInspectionChecklistByIdDoesNotExistTest() {
        Long inspectionChecklistId = 1L;

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionChecklistService.findInspectionChecklistById(inspectionChecklistId)
        );

        assertEquals("Inspection Checklist not found with id: " + inspectionChecklistId, exception.getMessage());
    }

    @Transactional
    @Test
    void findInspectionChecklistDisabledTest() {
        InspectionChecklist inspectionChecklist = new InspectionChecklist(1L, "type", false, testFdid);
        when(inspectionChecklistRepository.findByInspectionChecklistId(1L)).thenReturn(inspectionChecklist);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionChecklistService.findInspectionChecklistById(inspectionChecklist.getInspectionChecklistId())
        );

        assertEquals("Inspection Checklist with id: " + inspectionChecklist.getInspectionChecklistId() + " not enabled", exception.getMessage());
    }

    @Transactional
    @Test
    void updateInspectionChecklistTest() throws Exception {
        InspectionChecklist originalInspectionChecklist = new InspectionChecklist(1L, "type", true, testFdid);
        InspectionChecklist updateInspectionChecklist = new InspectionChecklist(1L, "new type", true, testFdid);
        when(inspectionChecklistRepository.findByInspectionChecklistId(updateInspectionChecklist.getInspectionChecklistId())).thenReturn(originalInspectionChecklist);
        when(inspectionChecklistRepository.save(updateInspectionChecklist)).thenReturn(updateInspectionChecklist);
        InspectionChecklistRequest updatedInspectionChecklistRequest = new InspectionChecklistRequest(updateInspectionChecklist);

        InspectionChecklistResponse updatedInspectionChecklistResponse = inspectionChecklistService.updateInspectionChecklist(updatedInspectionChecklistRequest);
        InspectionChecklist updatedInspectionChecklist = updatedInspectionChecklistResponse.getInspectionChecklist();

        assertNotNull(updatedInspectionChecklist);
        assertEquals(updateInspectionChecklist.getType(), updatedInspectionChecklist.getType());
        assertEquals(updateInspectionChecklist.isEnabled(), updatedInspectionChecklist.isEnabled());
        assertEquals(updateInspectionChecklist.getFdid(), updatedInspectionChecklist.getFdid());
    }

    @Transactional
    @Test
    void updateInspectionChecklistDoesNotExistTest() {
        InspectionChecklist inspectionChecklist = new InspectionChecklist(1L, "type", true, testFdid);
        when(inspectionChecklistRepository.findByInspectionChecklistId(inspectionChecklist.getInspectionChecklistId())).thenReturn(null);
        InspectionChecklistRequest inspectionChecklistRequest = new InspectionChecklistRequest(inspectionChecklist);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionChecklistService.updateInspectionChecklist(inspectionChecklistRequest)
        );

        assertEquals("Inspection Checklist not found with id: " + inspectionChecklist.getInspectionChecklistId(), exception.getMessage());
    }

    @Transactional
    @Test
    void deleteInspectionChecklistTest() {
        InspectionChecklist inspectionChecklist = new InspectionChecklist(1L, "type", true, testFdid);
        when(inspectionChecklistRepository.findByInspectionChecklistId(inspectionChecklist.getInspectionChecklistId())).thenReturn(inspectionChecklist);

        inspectionChecklistService.deleteInspectionChecklist(inspectionChecklist.getInspectionChecklistId());

        verify(inspectionChecklistRepository, times(1)).delete(inspectionChecklist);
    }

    @Transactional
    @Test
    void deleteInspectionChecklistDoesNotExistTest() {
        Long inspectionChecklistId = 1L;
        when(inspectionChecklistRepository.findByInspectionChecklistId(inspectionChecklistId)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionChecklistService.deleteInspectionChecklist(inspectionChecklistId)
        );

        assertEquals("Inspection Checklist not found with id: " + inspectionChecklistId, exception.getMessage());
    }
}
