package com.firerms.controller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firerms.controller.FireCodeController;
import com.firerms.entity.checklists.FireCode;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.EntityValidationException;
import com.firerms.exception.IdNotNullException;
import com.firerms.multiTenancy.RequestInterceptor;
import com.firerms.multiTenancy.TenantContext;
import com.firerms.request.FireCodeRequest;
import com.firerms.response.FireCodeResponse;
import com.firerms.security.service.CustomUserDetailsService;
import com.firerms.security.util.JwtUtility;
import com.firerms.service.FireCodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("local")
@AutoConfigureMockMvc
@WebMvcTest(FireCodeController.class)
public class FireCodeControllerIntTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private RequestInterceptor requestInterceptor;

    @MockBean
    private FireCodeService fireCodeService;

    @MockBean
    private JwtUtility jwtUtility;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    private final Long testFdid = 1L;

    @BeforeEach
    void setCurrentTenant() throws IOException {
        TenantContext.setCurrentTenant("1");
        when(requestInterceptor.preHandle(any(HttpServletRequest.class), any(HttpServletResponse.class), any(Object.class))).thenReturn(true);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void createFireCodeTest() throws Exception {
        FireCode fireCode = new FireCode(null, "code", "description", true, testFdid);
        FireCodeRequest fireCodeRequest = new FireCodeRequest(fireCode);
        FireCodeResponse fireCodeResponse = new FireCodeResponse(fireCode);
        String requestJson = new ObjectMapper().writeValueAsString(fireCodeRequest);
        when(fireCodeService.createFireCode(any(FireCodeRequest.class))).thenReturn(fireCodeResponse);

        MvcResult mvcResult = mockMvc.perform(post("/inspection/firecode/create")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)
        )
                .andExpect(status().isCreated())
                .andReturn();

        String expectedBody = new ObjectMapper().writeValueAsString(fireCodeResponse);
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(expectedBody, responseBody);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void createFireCodeWithNonNullIdTest() throws Exception {
        String errorMessage = "id must be null for new FireCode";
        FireCode fireCode = new FireCode(1L, "code", "description", true, testFdid);
        FireCodeRequest fireCodeRequest = new FireCodeRequest(fireCode);
        String requestJson = new ObjectMapper().writeValueAsString(fireCodeRequest);
        when(fireCodeService.createFireCode(any(FireCodeRequest.class)))
                .thenThrow(new IdNotNullException(errorMessage));

        MvcResult mvcResult = mockMvc.perform(post("/inspection/firecode/create")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)
        )
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString()
                .contains("{\"message\":\"" + errorMessage + "\""));
        assertTrue(mvcResult.getResponse().getContentAsString()
                .contains("\"status\":422"));
        assertTrue(mvcResult.getResponse().getContentAsString()
                .contains("\"timeStamp\":\""));
    }

    @Test
    void createFireCodeUnauthorizedTest() throws Exception {
        FireCode fireCode = new FireCode(null, "code", "description", true, testFdid);
        FireCodeRequest fireCodeRequest = new FireCodeRequest(fireCode);
        String requestJson = new ObjectMapper().writeValueAsString(fireCodeRequest);

        mockMvc.perform(post("/inspection/firecode/create")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)
        )
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void getFireCodeTest() throws Exception {
        FireCode fireCode = new FireCode(1L, "code", "description", true, testFdid);
        FireCodeResponse fireCodeResponse = new FireCodeResponse(fireCode);
        when(fireCodeService.findFireCodeById(fireCode.getFireCodeId())).thenReturn(fireCodeResponse);

        MvcResult mvcResult = mockMvc.perform(get("/inspection/firecode/{id}", fireCode.getFireCodeId()))
                .andExpect(status().isOk())
                .andReturn();

        String expectedBody = new ObjectMapper().writeValueAsString(fireCodeResponse);
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(expectedBody, responseBody);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void getFireCodeDoesNotExistTest() throws Exception {
        Long fireCodeId = 1L;
        String errorMessage = "Fire Code not found with id: " + fireCodeId;
        when(fireCodeService.findFireCodeById(fireCodeId)).thenThrow(new EntityNotFoundException(errorMessage));

        MvcResult mvcResult = mockMvc.perform(get("/inspection/firecode/{id}", fireCodeId))
                .andExpect(status().isNotFound())
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString()
                .contains("{\"message\":\"" + errorMessage + "\""));
        assertTrue(mvcResult.getResponse().getContentAsString()
                .contains("\"status\":404"));
        assertTrue(mvcResult.getResponse().getContentAsString()
                .contains("\"timeStamp\":\""));
    }

    @Test
    void getFireCodeUnauthorizedTest() throws Exception {
        Long fireCodeId = 1L;

        mockMvc.perform(get("/inspection/firecode/{id}", fireCodeId))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void updateFireCodeTest() throws Exception {
        FireCode fireCode = new FireCode(1L, "code", "description", true, testFdid);
        FireCodeRequest fireCodeRequest = new FireCodeRequest(fireCode);
        FireCodeResponse fireCodeResponse = new FireCodeResponse(fireCode);
        String requestJson = new ObjectMapper().writeValueAsString(fireCodeRequest);
        when(fireCodeService.updateFireCode(any(FireCodeRequest.class))).thenReturn(fireCodeResponse);

        MvcResult mvcResult = mockMvc.perform(put("/inspection/firecode/update")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)
        )
                .andExpect(status().isOk())
                .andReturn();

        String expectedBody = new ObjectMapper().writeValueAsString(fireCodeResponse);
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(expectedBody, responseBody);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void updateFireCodeDoesNotExistTest() throws Exception {
        FireCode fireCode = new FireCode(null, "code", "description", true, testFdid);
        FireCodeRequest fireCodeRequest = new FireCodeRequest(fireCode);
        String requestJson = new ObjectMapper().writeValueAsString(fireCodeRequest);
        String errorMessage = "Fire Code not found with id: " + fireCode.getFireCodeId();
        when(fireCodeService.updateFireCode(any(FireCodeRequest.class)))
                .thenThrow(new EntityNotFoundException(errorMessage));

        MvcResult mvcResult = mockMvc.perform(put("/inspection/firecode/update")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)
        )
                .andExpect(status().isNotFound())
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString()
                .contains("{\"message\":\"" + errorMessage +"\""));
        assertTrue(mvcResult.getResponse().getContentAsString()
                .contains("\"status\":404"));
        assertTrue(mvcResult.getResponse().getContentAsString()
                .contains("\"timeStamp\":\""));
    }

    @Test
    void updateFireCodeUnauthorizedTest() throws Exception {
        FireCode fireCode = new FireCode(null, "code", "description", true, testFdid);
        FireCodeRequest fireCodeRequest = new FireCodeRequest(fireCode);
        String requestJson = new ObjectMapper().writeValueAsString(fireCodeRequest);

        mockMvc.perform(put("/inspection/firecode/update")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)
        )
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void deleteFireCodeTest() throws Exception {
        Long fireCodeId = 1L;

        mockMvc.perform(delete("/inspection/firecode/{id}", fireCodeId))
                .andExpect(status().isNoContent())
                .andReturn();

        verify(fireCodeService, times(1)).deleteFireCode(fireCodeId);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void deleteFireCodeDoesNotExistTest() throws Exception {
        Long fireCodeId = 1L;
        String errorMessage = "Fire Code not found with id: " + fireCodeId;
        doThrow(new EntityNotFoundException(errorMessage))
                .when(fireCodeService).deleteFireCode(fireCodeId);

        MvcResult mvcResult = mockMvc.perform(delete("/inspection/firecode/{id}", fireCodeId))
                .andExpect(status().isNotFound())
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString()
                .contains("{\"message\":\"" + errorMessage + "\""));
        assertTrue(mvcResult.getResponse().getContentAsString()
                .contains("\"status\":404"));
        assertTrue(mvcResult.getResponse().getContentAsString()
                .contains("\"timeStamp\":\""));
    }

    @Test
    void deleteFireCodeUnauthorizedTest() throws Exception {
        Long fireCodeId = 1L;

        mockMvc.perform(delete("/fireCode/{id}", fireCodeId))
                .andExpect(status().isForbidden())
                .andReturn();
    }
}
