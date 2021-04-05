package com.firerms.service.unit;

import com.firerms.entity.checklists.FireCode;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.IdNotNullException;
import com.firerms.multiTenancy.TenantContext;
import com.firerms.repository.FireCodeRepository;
import com.firerms.request.FireCodeRequest;
import com.firerms.response.FireCodeResponse;
import com.firerms.service.FireCodeService;
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
public class FireCodeServiceUnitTests {

    @Autowired
    private FireCodeService fireCodeService;

    @MockBean
    private FireCodeRepository fireCodeRepository;

    private final Long testFdid = 1L;

    @BeforeEach
    void setCurrentTenant() {
        TenantContext.setCurrentTenant("1");
    }

    @Transactional
    @Test
    void createFireCodeTest() throws Exception {
        FireCode fireCode = new FireCode(null, "code", "description", true, testFdid);
        when(fireCodeRepository.save(fireCode)).thenReturn(fireCode);
        FireCodeRequest fireCodeRequest = new FireCodeRequest(fireCode);

        FireCodeResponse savedFireCodeResponse = fireCodeService.createFireCode(fireCodeRequest);
        FireCode savedFireCode = savedFireCodeResponse.getFireCode();

        assertNotNull(savedFireCode);
        assertEquals(fireCode.getCode(), savedFireCode.getCode());
        assertEquals(fireCode.getDescription(), savedFireCode.getDescription());
        assertEquals(fireCode.isEnabled(), savedFireCode.isEnabled());
        assertEquals(fireCode.getFdid(), savedFireCode.getFdid());
    }

    @Transactional
    @Test
    void createFireCodeWithNonNullIdTest() {
        FireCode fireCode = new FireCode(1L, "code", "description", true, testFdid);
        FireCodeRequest fireCodeRequest = new FireCodeRequest(fireCode);

        IdNotNullException exception = assertThrows(
                IdNotNullException.class,
                () -> fireCodeService.createFireCode(fireCodeRequest)
        );

        assertEquals("id must be null for new Fire Code", exception.getMessage());
    }

    @Transactional
    @Test
    void findFireCodeByIdTest() throws Exception {
        FireCode fireCode = new FireCode(1L, "code", "description", true, testFdid);
        when(fireCodeRepository.findByFireCodeId(1L)).thenReturn(fireCode);

        FireCodeResponse fireCodeResponse = fireCodeService.findFireCodeById(1L);
        FireCode foundFireCode = fireCodeResponse.getFireCode();

        assertNotNull(foundFireCode);
        assertEquals(fireCode.getCode(), foundFireCode.getCode());
        assertEquals(fireCode.getDescription(), foundFireCode.getDescription());
        assertEquals(fireCode.isEnabled(), foundFireCode.isEnabled());
        assertEquals(fireCode.getFdid(), foundFireCode.getFdid());
    }

    @Transactional
    @Test
    void findFireCodeByIdDoesNotExistTest() {
        Long fireCodeId = 1L;

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> fireCodeService.findFireCodeById(fireCodeId)
        );

        assertEquals("Fire Code not found with id: " + fireCodeId, exception.getMessage());
    }

    @Transactional
    @Test
    void updateFireCodeTest() throws Exception {
        FireCode originalFireCode = new FireCode(1L, "code", "description", true, testFdid);
        FireCode updateFireCode = new FireCode(1L, "new code", "new description", true, testFdid);
        when(fireCodeRepository.findByFireCodeId(updateFireCode.getFireCodeId())).thenReturn(originalFireCode);
        when(fireCodeRepository.save(updateFireCode)).thenReturn(updateFireCode);
        FireCodeRequest updatedFireCodeRequest = new FireCodeRequest(updateFireCode);

        FireCodeResponse updatedFireCodeResponse = fireCodeService.updateFireCode(updatedFireCodeRequest);
        FireCode updatedFireCode = updatedFireCodeResponse.getFireCode();

        assertNotNull(updatedFireCode);
        assertEquals(updateFireCode.getCode(), updatedFireCode.getCode());
        assertEquals(updateFireCode.getDescription(), updatedFireCode.getDescription());
        assertEquals(updateFireCode.isEnabled(), updatedFireCode.isEnabled());
        assertEquals(updateFireCode.getFdid(), updatedFireCode.getFdid());
    }

    @Transactional
    @Test
    void updateFireCodeDoesNotExistTest() {
        FireCode fireCode = new FireCode(1L, "code", "description", true, testFdid);
        when(fireCodeRepository.findByFireCodeId(fireCode.getFireCodeId())).thenReturn(null);
        FireCodeRequest fireCodeRequest = new FireCodeRequest(fireCode);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> fireCodeService.updateFireCode(fireCodeRequest)
        );

        assertEquals("Fire Code not found with id: " + fireCode.getFireCodeId(), exception.getMessage());
    }

    @Transactional
    @Test
    void deleteFireCodeTest() {
        FireCode fireCode = new FireCode(1L, "code", "description", true, testFdid);
        when(fireCodeRepository.findByFireCodeId(fireCode.getFireCodeId())).thenReturn(fireCode);

        fireCodeService.deleteFireCode(fireCode.getFireCodeId());

        verify(fireCodeRepository, times(1)).delete(fireCode);
    }

    @Transactional
    @Test
    void deleteFireCodeDoesNotExistTest() {
        Long fireCodeId = 1L;
        when(fireCodeRepository.findByFireCodeId(fireCodeId)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> fireCodeService.deleteFireCode(fireCodeId)
        );

        assertEquals("Fire Code not found with id: " + fireCodeId, exception.getMessage());
    }
}
