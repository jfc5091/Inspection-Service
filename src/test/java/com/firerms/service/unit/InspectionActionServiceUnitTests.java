package com.firerms.service.unit;

import com.firerms.entity.inspections.Inspection;
import com.firerms.entity.inspections.InspectionAction;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.IdNotNullException;
import com.firerms.multiTenancy.TenantContext;
import com.firerms.repository.InspectionActionRepository;
import com.firerms.repository.InspectionRepository;
import com.firerms.request.InspectionActionRequest;
import com.firerms.response.InspectionActionResponse;
import com.firerms.service.InspectionActionService;
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
public class InspectionActionServiceUnitTests {

    @Autowired
    private InspectionActionService inspectionActionService;

    @Autowired
    private InspectionRepository inspectionRepository;

    @MockBean
    private InspectionActionRepository inspectionActionRepository;

    private final Long testFdid = 1L;

    @BeforeEach
    void setCurrentTenant() {
        TenantContext.setCurrentTenant(testFdid.toString());
    }

    @Transactional
    @Test
    void createInspectionActionTest() throws Exception {
        InspectionAction inspectionAction = new InspectionAction(null, 1L, "action", new Date(), "description", "narrative", testFdid);
        when(inspectionActionRepository.save(inspectionAction)).thenReturn(inspectionAction);
        when(inspectionRepository.findByInspectionId(inspectionAction.getInspectionId())).thenReturn(new Inspection());
        InspectionActionRequest inspectionActionRequest = new InspectionActionRequest(inspectionAction);

        InspectionActionResponse createdInspectionActionResponse = inspectionActionService.createInspectionAction(inspectionActionRequest);
        InspectionAction createdInspectionAction = createdInspectionActionResponse.getInspectionAction();

        assertNotNull(createdInspectionAction);
        assertEquals(inspectionAction.getInspectionId(), createdInspectionAction.getInspectionId());
        assertEquals(inspectionAction.getAction(), createdInspectionAction.getAction());
        assertEquals(inspectionAction.getDate(), createdInspectionAction.getDate());
        assertEquals(inspectionAction.getDescription(), createdInspectionAction.getDescription());
        assertEquals(inspectionAction.getNarrative(), createdInspectionAction.getNarrative());
        assertEquals(inspectionAction.getFdid(), createdInspectionAction.getFdid());
    }

    @Transactional
    @Test
    void createInspectionActionWithNonNullIdTest() {
        InspectionAction inspectionAction = new InspectionAction(1L, 1L, "action", new Date(), "description", "narrative", testFdid);
        InspectionActionRequest inspectionActionRequest = new InspectionActionRequest(inspectionAction);

        IdNotNullException exception = assertThrows(
                IdNotNullException.class,
                () -> inspectionActionService.createInspectionAction(inspectionActionRequest)
        );

        assertEquals("id must be null for new Inspection Action", exception.getMessage());
    }

    @Transactional
    @Test
    void createInspectionActionInspectionDoesNotExistTest() {
        InspectionAction inspectionAction = new InspectionAction(1L, 1L, "action", new Date(), "description", "narrative", testFdid);
        InspectionActionRequest inspectionActionRequest = new InspectionActionRequest(inspectionAction);
        when(inspectionRepository.findByInspectionId(inspectionAction.getInspectionId())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionActionService.createInspectionAction(inspectionActionRequest)
        );

        assertEquals("Inspection Action not found with id: " + inspectionAction.getInspectionId(), exception.getMessage());
    }

    @Transactional
    @Test
    void findInspectionActionByIdTest() throws Exception {
        InspectionAction inspectionAction = new InspectionAction(1L, 1L, "action", new Date(), "description", "narrative", testFdid);
        when(inspectionActionRepository.findByInspectionActionId(1L)).thenReturn(inspectionAction);

        InspectionActionResponse inspectionActionResponse = inspectionActionService.findInspectionActionById(1L);
        InspectionAction foundInspectionAction = inspectionActionResponse.getInspectionAction();

        assertNotNull(foundInspectionAction);
        assertEquals(inspectionAction.getInspectionId(), foundInspectionAction.getInspectionId());
        assertEquals(inspectionAction.getAction(), foundInspectionAction.getAction());
        assertEquals(inspectionAction.getDate(), foundInspectionAction.getDate());
        assertEquals(inspectionAction.getDescription(), foundInspectionAction.getDescription());
        assertEquals(inspectionAction.getNarrative(), foundInspectionAction.getNarrative());
        assertEquals(inspectionAction.getFdid(), foundInspectionAction.getFdid());
    }

    @Transactional
    @Test
    void findInspectionActionByIdDoesNotExistTest() {
        Long inspectionActionId = 1L;

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionActionService.findInspectionActionById(inspectionActionId)
        );

        assertEquals("Inspection Action not found with id: " + inspectionActionId, exception.getMessage());
    }

    @Transactional
    @Test
    void updateInspectionActionTest() throws Exception {
        InspectionAction originalInspectionAction = new InspectionAction(1L, 1L, "action", new Date(), "description", "narrative", testFdid);
        InspectionAction updateInspectionAction = new InspectionAction(1L, 1L, "new action", new Date(), "new description", "narrative", testFdid);
        when(inspectionActionRepository.findByInspectionActionId(updateInspectionAction.getInspectionActionId())).thenReturn(originalInspectionAction);
        when(inspectionActionRepository.save(updateInspectionAction)).thenReturn(updateInspectionAction);
        when(inspectionRepository.findByInspectionId(originalInspectionAction.getInspectionId())).thenReturn(new Inspection());
        InspectionActionRequest updatedInspectionActionRequest = new InspectionActionRequest(updateInspectionAction);

        InspectionActionResponse updatedInspectionActionResponse = inspectionActionService.updateInspectionAction(updatedInspectionActionRequest);
        InspectionAction updatedInspectionAction = updatedInspectionActionResponse.getInspectionAction();

        assertNotNull(updatedInspectionAction);
        assertEquals(updateInspectionAction.getInspectionId(), updatedInspectionAction.getInspectionId());
        assertEquals(updateInspectionAction.getAction(), updatedInspectionAction.getAction());
        assertEquals(updateInspectionAction.getDate(), updatedInspectionAction.getDate());
        assertEquals(updateInspectionAction.getDescription(), updatedInspectionAction.getDescription());
        assertEquals(updateInspectionAction.getNarrative(), updatedInspectionAction.getNarrative());
        assertEquals(updateInspectionAction.getFdid(), updatedInspectionAction.getFdid());
    }

    @Transactional
    @Test
    void updateInspectionActionDoesNotExistTest() {
        InspectionAction inspectionAction = new InspectionAction(1L, 1L, "action", new Date(), "description", "narrative", testFdid);
        when(inspectionActionRepository.findByInspectionActionId(inspectionAction.getInspectionActionId())).thenReturn(null);
        InspectionActionRequest inspectionActionRequest = new InspectionActionRequest(inspectionAction);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionActionService.updateInspectionAction(inspectionActionRequest)
        );

        assertEquals("Inspection Action not found with id: " + inspectionAction.getInspectionActionId(), exception.getMessage());
    }

    @Transactional
    @Test
    void updateInspectionActionInspectionDoesNotExistTest() {
        InspectionAction inspectionAction = new InspectionAction(1L, 1L, "action", new Date(), "description", "narrative", testFdid);
        InspectionActionRequest inspectionActionRequest = new InspectionActionRequest(inspectionAction);
        when(inspectionRepository.findByInspectionId(inspectionAction.getInspectionId())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionActionService.updateInspectionAction(inspectionActionRequest)
        );

        assertEquals("Inspection Action not found with id: " + inspectionAction.getInspectionId(), exception.getMessage());
    }

    @Transactional
    @Test
    void deleteInspectionActionTest() {
        InspectionAction inspectionAction = new InspectionAction(1L, 1L, "action", new Date(), "description", "narrative", testFdid);
        when(inspectionActionRepository.findByInspectionActionId(inspectionAction.getInspectionActionId())).thenReturn(inspectionAction);

        inspectionActionService.deleteInspectionAction(inspectionAction.getInspectionActionId());

        verify(inspectionActionRepository, times(1)).delete(inspectionAction);
    }

    @Transactional
    @Test
    void deleteInspectionActionDoesNotExistTest() {
        Long inspectionActionId = 1L;
        when(inspectionActionRepository.findByInspectionActionId(inspectionActionId)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionActionService.deleteInspectionAction(inspectionActionId)
        );

        assertEquals("Inspection Action not found with id: " + inspectionActionId, exception.getMessage());
    }
}
