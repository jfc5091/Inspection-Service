package com.firerms.controller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.firerms.controller.InspectionViolationController;
import com.firerms.entity.checklists.InspectionViolation;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.IdNotNullException;
import com.firerms.multiTenancy.RequestInterceptor;
import com.firerms.multiTenancy.TenantContext;
import com.firerms.request.InspectionViolationRequest;
import com.firerms.response.InspectionViolationResponse;
import com.firerms.security.service.CustomUserDetailsService;
import com.firerms.security.util.JwtUtility;
import com.firerms.service.InspectionViolationService;
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
import java.util.Date;

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
@WebMvcTest(InspectionViolationController.class)
public class InspectionViolationControllerIntTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private RequestInterceptor requestInterceptor;

    @MockBean
    private InspectionViolationService inspectionViolationService;

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
    void createInspectionViolationTest() throws Exception {
        InspectionViolation inspectionViolation = new InspectionViolation(null, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        InspectionViolationRequest inspectionViolationRequest = new InspectionViolationRequest(inspectionViolation);
        InspectionViolationResponse inspectionViolationResponse = new InspectionViolationResponse(inspectionViolation);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionViolationRequest);
        when(inspectionViolationService.createInspectionViolation(any(InspectionViolationRequest.class))).thenReturn(inspectionViolationResponse);

        MvcResult mvcResult = mockMvc.perform(post("/inspection/violation/create")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)
        )
                .andExpect(status().isCreated())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String expectedBody = objectMapper.writeValueAsString(inspectionViolationResponse);
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(expectedBody, responseBody);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void createInspectionViolationWithNonNullIdTest() throws Exception {
        String errorMessage = "id must be null for new InspectionViolation";
        InspectionViolation inspectionViolation = new InspectionViolation(1L, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        InspectionViolationRequest inspectionViolationRequest = new InspectionViolationRequest(inspectionViolation);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionViolationRequest);
        when(inspectionViolationService.createInspectionViolation(any(InspectionViolationRequest.class)))
                .thenThrow(new IdNotNullException(errorMessage));

        MvcResult mvcResult = mockMvc.perform(post("/inspection/violation/create")
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
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void createInspectionViolationChildEntityDoesNotExistTest() throws Exception {
        InspectionViolation inspectionViolation = new InspectionViolation(1L, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        InspectionViolationRequest inspectionViolationRequest = new InspectionViolationRequest(inspectionViolation);
        String errorMessage = "Fire Code not found with id: " + inspectionViolation.getFireCodeId();
        String requestJson = new ObjectMapper().writeValueAsString(inspectionViolationRequest);
        when(inspectionViolationService.createInspectionViolation(any(InspectionViolationRequest.class)))
                .thenThrow(new EntityNotFoundException(errorMessage));

        MvcResult mvcResult = mockMvc.perform(post("/inspection/violation/create")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)
        )
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
    void createInspectionViolationUnauthorizedTest() throws Exception {
        InspectionViolation inspectionViolation = new InspectionViolation(null, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        InspectionViolationRequest inspectionViolationRequest = new InspectionViolationRequest(inspectionViolation);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionViolationRequest);

        mockMvc.perform(post("/inspection/violation/create")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)
        )
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void getInspectionViolationTest() throws Exception {
        InspectionViolation inspectionViolation = new InspectionViolation(1L, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        InspectionViolationResponse inspectionViolationResponse = new InspectionViolationResponse(inspectionViolation);
        when(inspectionViolationService.findInspectionViolationById(inspectionViolation.getInspectionViolationId())).thenReturn(inspectionViolationResponse);

        MvcResult mvcResult = mockMvc.perform(get("/inspection/violation/{id}", inspectionViolation.getInspectionViolationId()))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String expectedBody = objectMapper.writeValueAsString(inspectionViolationResponse);
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(expectedBody, responseBody);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void getInspectionViolationDoesNotExistTest() throws Exception {
        Long inspectionViolationId = 1L;
        String errorMessage = "Inspection Violation not found with id: " + inspectionViolationId;
        when(inspectionViolationService.findInspectionViolationById(inspectionViolationId)).thenThrow(new EntityNotFoundException(errorMessage));

        MvcResult mvcResult = mockMvc.perform(get("/inspection/violation/{id}", inspectionViolationId))
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
    void getInspectionViolationUnauthorizedTest() throws Exception {
        Long inspectionViolationId = 1L;

        mockMvc.perform(get("/inspection/violation/{id}", inspectionViolationId))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void updateInspectionViolationTest() throws Exception {
        InspectionViolation inspectionViolation = new InspectionViolation(1L, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        InspectionViolationRequest inspectionViolationRequest = new InspectionViolationRequest(inspectionViolation);
        InspectionViolationResponse inspectionViolationResponse = new InspectionViolationResponse(inspectionViolation);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionViolationRequest);
        when(inspectionViolationService.updateInspectionViolation(any(InspectionViolationRequest.class))).thenReturn(inspectionViolationResponse);

        MvcResult mvcResult = mockMvc.perform(put("/inspection/violation/update")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)
        )
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String expectedBody = objectMapper.writeValueAsString(inspectionViolationResponse);
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(expectedBody, responseBody);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void updateInspectionViolationDoesNotExistTest() throws Exception {
        InspectionViolation inspectionViolation = new InspectionViolation(null, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        InspectionViolationRequest inspectionViolationRequest = new InspectionViolationRequest(inspectionViolation);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionViolationRequest);
        String errorMessage = "Inspection Violation not found with id: " + inspectionViolation.getInspectionViolationId();
        when(inspectionViolationService.updateInspectionViolation(any(InspectionViolationRequest.class)))
                .thenThrow(new EntityNotFoundException(errorMessage));

        MvcResult mvcResult = mockMvc.perform(put("/inspection/violation/update")
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
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void updateInspectionViolationChildEntityDoesNotExistTest() throws Exception {
        InspectionViolation inspectionViolation = new InspectionViolation(1L, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        InspectionViolationRequest inspectionViolationRequest = new InspectionViolationRequest(inspectionViolation);
        String errorMessage = "Fire Code not found with id: " + inspectionViolation.getFireCodeId();
        String requestJson = new ObjectMapper().writeValueAsString(inspectionViolationRequest);
        when(inspectionViolationService.updateInspectionViolation(any(InspectionViolationRequest.class)))
                .thenThrow(new EntityNotFoundException(errorMessage));

        MvcResult mvcResult = mockMvc.perform(put("/inspection/violation/update")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)
        )
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
    void updateInspectionViolationUnauthorizedTest() throws Exception {
        InspectionViolation inspectionViolation = new InspectionViolation(null, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        InspectionViolationRequest inspectionViolationRequest = new InspectionViolationRequest(inspectionViolation);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionViolationRequest);

        mockMvc.perform(put("/inspection/violation/update")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)
        )
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void deleteInspectionViolationTest() throws Exception {
        Long inspectionViolationId = 1L;

        mockMvc.perform(delete("/inspection/violation/{id}", inspectionViolationId))
                .andExpect(status().isNoContent())
                .andReturn();

        verify(inspectionViolationService, times(1)).deleteInspectionViolation(inspectionViolationId);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void deleteInspectionViolationDoesNotExistTest() throws Exception {
        Long inspectionViolationId = 1L;
        String errorMessage = "Inspection Violation not found with id: " + inspectionViolationId;
        doThrow(new EntityNotFoundException(errorMessage))
                .when(inspectionViolationService).deleteInspectionViolation(inspectionViolationId);

        MvcResult mvcResult = mockMvc.perform(delete("/inspection/violation/{id}", inspectionViolationId))
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
    void deleteInspectionViolationUnauthorizedTest() throws Exception {
        Long inspectionViolationId = 1L;

        mockMvc.perform(delete("/inspectionViolation/{id}", inspectionViolationId))
                .andExpect(status().isForbidden())
                .andReturn();
    }
}
