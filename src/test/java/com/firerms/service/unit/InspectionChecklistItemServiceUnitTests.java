package com.firerms.service.unit;

import com.firerms.entity.checklists.FireCode;
import com.firerms.entity.checklists.InspectionChecklist;
import com.firerms.entity.checklists.InspectionChecklistItem;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.IdNotNullException;
import com.firerms.multiTenancy.TenantContext;
import com.firerms.repository.FireCodeRepository;
import com.firerms.repository.InspectionChecklistItemRepository;
import com.firerms.repository.InspectionChecklistRepository;
import com.firerms.request.InspectionChecklistItemRequest;
import com.firerms.response.InspectionChecklistItemResponse;
import com.firerms.service.InspectionChecklistItemService;
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
public class InspectionChecklistItemServiceUnitTests {

    @Autowired
    private InspectionChecklistItemService inspectionChecklistItemService;

    @MockBean
    private InspectionChecklistItemRepository inspectionChecklistItemRepository;

    @MockBean
    private InspectionChecklistRepository inspectionChecklistRepository;

    @MockBean
    private FireCodeRepository fireCodeRepository;

    private final Long testFdid = 1L;

    @BeforeEach
    void setCurrentTenant() {
        TenantContext.setCurrentTenant("1");
    }

    @Transactional
    @Test
    void createInspectionChecklistItemTest() throws Exception {
        InspectionChecklistItem inspectionChecklistItem = new InspectionChecklistItem(null, 1L, 1L, "description", testFdid);
        when(inspectionChecklistItemRepository.save(inspectionChecklistItem)).thenReturn(inspectionChecklistItem);
        when(inspectionChecklistRepository.findByInspectionChecklistId(inspectionChecklistItem.getInspectionChecklistId())).thenReturn(new InspectionChecklist());
        when(fireCodeRepository.findByFireCodeId(inspectionChecklistItem.getFireCodeId())).thenReturn(new FireCode());
        InspectionChecklistItemRequest inspectionChecklistItemRequest = new InspectionChecklistItemRequest(inspectionChecklistItem);

        InspectionChecklistItemResponse savedInspectionChecklistItemResponse = inspectionChecklistItemService.createInspectionChecklistItem(inspectionChecklistItemRequest);
        InspectionChecklistItem savedInspectionChecklistItem = savedInspectionChecklistItemResponse.getInspectionChecklistItem();

        assertNotNull(savedInspectionChecklistItem);
        assertEquals(inspectionChecklistItem.getInspectionChecklistId(), savedInspectionChecklistItem.getInspectionChecklistId());
        assertEquals(inspectionChecklistItem.getFireCodeId(), savedInspectionChecklistItem.getFireCodeId());
        assertEquals(inspectionChecklistItem.getDescription(), savedInspectionChecklistItem.getDescription());
        assertEquals(inspectionChecklistItem.getFdid(), savedInspectionChecklistItem.getFdid());
    }

    @Transactional
    @Test
    void createInspectionChecklistItemWithNonNullIdTest() {
        InspectionChecklistItem inspectionChecklistItem = new InspectionChecklistItem(1L, 1L, 1L, "description", testFdid);
        InspectionChecklistItemRequest inspectionChecklistItemRequest = new InspectionChecklistItemRequest(inspectionChecklistItem);

        IdNotNullException exception = assertThrows(
                IdNotNullException.class,
                () -> inspectionChecklistItemService.createInspectionChecklistItem(inspectionChecklistItemRequest)
        );

        assertEquals("id must be null for new Inspection Checklist Item", exception.getMessage());
    }

    @Transactional
    @Test
    void createInspectionChecklistItemInspectionChecklistIdDoesNotExistTest() {
        InspectionChecklistItem inspectionChecklistItem = new InspectionChecklistItem(null, 1L, 1L, "description", testFdid);
        InspectionChecklistItemRequest inspectionChecklistItemRequest = new InspectionChecklistItemRequest(inspectionChecklistItem);
        when(inspectionChecklistRepository.findByInspectionChecklistId(inspectionChecklistItem.getInspectionChecklistId())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionChecklistItemService.createInspectionChecklistItem(inspectionChecklistItemRequest)
        );

        assertEquals("Inspection Checklist not found with id: " + inspectionChecklistItem.getInspectionChecklistId(), exception.getMessage());
    }

    @Transactional
    @Test
    void createInspectionChecklistItemFireCodeIdDoesNotExistTest() {
        InspectionChecklistItem inspectionChecklistItem = new InspectionChecklistItem(null, 1L, 1L, "description", testFdid);
        InspectionChecklistItemRequest inspectionChecklistItemRequest = new InspectionChecklistItemRequest(inspectionChecklistItem);
        when(inspectionChecklistRepository.findByInspectionChecklistId(inspectionChecklistItem.getInspectionChecklistId())).thenReturn(new InspectionChecklist());
        when(fireCodeRepository.findByFireCodeId(inspectionChecklistItem.getFireCodeId())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionChecklistItemService.createInspectionChecklistItem(inspectionChecklistItemRequest)
        );

        assertEquals("Fire Code not found with id: " + inspectionChecklistItem.getFireCodeId(), exception.getMessage());
    }

    @Transactional
    @Test
    void findInspectionChecklistItemByIdTest() throws Exception {
        InspectionChecklistItem inspectionChecklistItem = new InspectionChecklistItem(1L, 1L, 1L, "description", testFdid);
        when(inspectionChecklistItemRepository.findByInspectionChecklistItemId(1L)).thenReturn(inspectionChecklistItem);

        InspectionChecklistItemResponse inspectionChecklistItemResponse = inspectionChecklistItemService.findInspectionChecklistItemById(1L);
        InspectionChecklistItem foundInspectionChecklistItem = inspectionChecklistItemResponse.getInspectionChecklistItem();

        assertNotNull(foundInspectionChecklistItem);
        assertEquals(inspectionChecklistItem.getInspectionChecklistId(), foundInspectionChecklistItem.getInspectionChecklistId());
        assertEquals(inspectionChecklistItem.getFireCodeId(), foundInspectionChecklistItem.getFireCodeId());
        assertEquals(inspectionChecklistItem.getDescription(), foundInspectionChecklistItem.getDescription());
        assertEquals(inspectionChecklistItem.getFdid(), foundInspectionChecklistItem.getFdid());
    }

    @Transactional
    @Test
    void findInspectionChecklistItemByIdDoesNotExistTest() {
        Long inspectionChecklistItemId = 1L;

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionChecklistItemService.findInspectionChecklistItemById(inspectionChecklistItemId)
        );

        assertEquals("Inspection Checklist Item not found with id: " + inspectionChecklistItemId, exception.getMessage());
    }

    @Transactional
    @Test
    void updateInspectionChecklistItemTest() throws Exception {
        InspectionChecklistItem originalInspectionChecklistItem = new InspectionChecklistItem(1L, 1L, 1L, "description", testFdid);
        InspectionChecklistItem updateInspectionChecklistItem = new InspectionChecklistItem(1L, 1L, 1L, "updated description", testFdid);
        when(inspectionChecklistItemRepository.findByInspectionChecklistItemId(updateInspectionChecklistItem.getInspectionChecklistItemId())).thenReturn(originalInspectionChecklistItem);
        when(inspectionChecklistRepository.findByInspectionChecklistId(originalInspectionChecklistItem.getInspectionChecklistId())).thenReturn(new InspectionChecklist());
        when(fireCodeRepository.findByFireCodeId(originalInspectionChecklistItem.getFireCodeId())).thenReturn(new FireCode());
        when(inspectionChecklistItemRepository.save(updateInspectionChecklistItem)).thenReturn(updateInspectionChecklistItem);
        InspectionChecklistItemRequest updatedInspectionChecklistItemRequest = new InspectionChecklistItemRequest(updateInspectionChecklistItem);

        InspectionChecklistItemResponse updatedInspectionChecklistItemResponse = inspectionChecklistItemService.updateInspectionChecklistItem(updatedInspectionChecklistItemRequest);
        InspectionChecklistItem updatedInspectionChecklistItem = updatedInspectionChecklistItemResponse.getInspectionChecklistItem();

        assertNotNull(updatedInspectionChecklistItem);
        assertEquals(updateInspectionChecklistItem.getInspectionChecklistId(), updatedInspectionChecklistItem.getInspectionChecklistId());
        assertEquals(updateInspectionChecklistItem.getFireCodeId(), updatedInspectionChecklistItem.getFireCodeId());
        assertEquals(updateInspectionChecklistItem.getDescription(), updatedInspectionChecklistItem.getDescription());
        assertEquals(updateInspectionChecklistItem.getFdid(), updatedInspectionChecklistItem.getFdid());
    }

    @Transactional
    @Test
    void updateInspectionChecklistItemDoesNotExistTest() {
        InspectionChecklistItem inspectionChecklistItem = new InspectionChecklistItem(1L, 1L, 1L, "description", testFdid);
        when(inspectionChecklistItemRepository.findByInspectionChecklistItemId(inspectionChecklistItem.getInspectionChecklistItemId())).thenReturn(null);
        InspectionChecklistItemRequest inspectionChecklistItemRequest = new InspectionChecklistItemRequest(inspectionChecklistItem);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionChecklistItemService.updateInspectionChecklistItem(inspectionChecklistItemRequest)
        );

        assertEquals("Inspection Checklist Item not found with id: " + inspectionChecklistItem.getInspectionChecklistItemId(), exception.getMessage());
    }

    @Transactional
    @Test
    void updateInspectionChecklistItemInspectionChecklistIdDoesNotExistTest() {
        InspectionChecklistItem originalInspectionChecklistItem = new InspectionChecklistItem(1L, 1L, 1L, "description", testFdid);
        InspectionChecklistItem updateInspectionChecklistItem = new InspectionChecklistItem(1L, 1L, 1L, "updated description", testFdid);
        when(inspectionChecklistItemRepository.findByInspectionChecklistItemId(updateInspectionChecklistItem.getInspectionChecklistItemId())).thenReturn(originalInspectionChecklistItem);
        InspectionChecklistItemRequest inspectionChecklistItemRequest = new InspectionChecklistItemRequest(updateInspectionChecklistItem);
        when(inspectionChecklistRepository.findByInspectionChecklistId(updateInspectionChecklistItem.getInspectionChecklistId())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionChecklistItemService.updateInspectionChecklistItem(inspectionChecklistItemRequest)
        );

        assertEquals("Inspection Checklist not found with id: " + originalInspectionChecklistItem.getInspectionChecklistId(), exception.getMessage());
    }

    @Transactional
    @Test
    void updateInspectionChecklistItemFireCodeIdDoesNotExistTest() {
        InspectionChecklistItem originalInspectionChecklistItem = new InspectionChecklistItem(1L, 1L, 1L, "description", testFdid);
        InspectionChecklistItem updateInspectionChecklistItem = new InspectionChecklistItem(1L, 1L, 1L, "updated description", testFdid);
        when(inspectionChecklistItemRepository.findByInspectionChecklistItemId(updateInspectionChecklistItem.getInspectionChecklistItemId())).thenReturn(originalInspectionChecklistItem);
        InspectionChecklistItemRequest inspectionChecklistItemRequest = new InspectionChecklistItemRequest(updateInspectionChecklistItem);
        when(inspectionChecklistRepository.findByInspectionChecklistId(updateInspectionChecklistItem.getInspectionChecklistId())).thenReturn(new InspectionChecklist());
        when(fireCodeRepository.findByFireCodeId(updateInspectionChecklistItem.getFireCodeId())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionChecklistItemService.updateInspectionChecklistItem(inspectionChecklistItemRequest)
        );

        assertEquals("Fire Code not found with id: " + updateInspectionChecklistItem.getFireCodeId(), exception.getMessage());
    }

    @Transactional
    @Test
    void deleteInspectionChecklistItemTest() {
        InspectionChecklistItem inspectionChecklistItem = new InspectionChecklistItem(1L, 1L, 1L, "description", testFdid);
        when(inspectionChecklistItemRepository.findByInspectionChecklistItemId(inspectionChecklistItem.getInspectionChecklistItemId())).thenReturn(inspectionChecklistItem);

        inspectionChecklistItemService.deleteInspectionChecklistItem(inspectionChecklistItem.getInspectionChecklistItemId());

        verify(inspectionChecklistItemRepository, times(1)).delete(inspectionChecklistItem);
    }

    @Transactional
    @Test
    void deleteInspectionChecklistItemDoesNotExistTest() {
        Long inspectionChecklistItemId = 1L;
        when(inspectionChecklistItemRepository.findByInspectionChecklistItemId(inspectionChecklistItemId)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> inspectionChecklistItemService.deleteInspectionChecklistItem(inspectionChecklistItemId)
        );

        assertEquals("Inspection Checklist Item not found with id: " + inspectionChecklistItemId, exception.getMessage());
    }
}
