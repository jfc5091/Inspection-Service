package com.firerms.controller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firerms.controller.InspectionController;
import com.firerms.entity.inspections.Inspection;
import com.firerms.entity.inspections.Inspector;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.IdNotNullException;
import com.firerms.multiTenancy.RequestInterceptor;
import com.firerms.multiTenancy.TenantContext;
import com.firerms.request.InspectionRequest;
import com.firerms.response.InspectionResponse;
import com.firerms.security.entity.User;
import com.firerms.security.service.CustomUserDetailsService;
import com.firerms.security.util.JwtUtility;
import com.firerms.service.InspectionService;
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
@WebMvcTest(InspectionController.class)
public class InspectionControllerIntTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private RequestInterceptor requestInterceptor;

    @MockBean
    private InspectionService inspectionService;

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
    void createInspectionTest() throws Exception {
        Inspector inspector = new Inspector(null, 1L, "first", "last", "2035559944", 1L);
        Inspection inspection = new Inspection(null, 1L, inspector, 1L, "status",
                "narrative", "occupantSignatureUrl", "inspectorSignatureUrl", testFdid);
        InspectionRequest inspectionRequest = new InspectionRequest(inspection);
        InspectionResponse inspectionResponse = new InspectionResponse(inspection);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionRequest);
        when(inspectionService.createInspection(any(InspectionRequest.class))).thenReturn(inspectionResponse);

        MvcResult mvcResult = mockMvc.perform(post("/inspection/create")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)
        )
                .andExpect(status().isCreated())
                .andReturn();

        String expectedBody = new ObjectMapper().writeValueAsString(inspectionResponse);
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(expectedBody, responseBody);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void createInspectionWithNonNullIdTest() throws Exception {
        String errorMessage = "id must be null for new Inspection";
        Inspector inspector = new Inspector(null, 1L, "first", "last", "2035559944", 1L);
        Inspection inspection = new Inspection(1L, 1L, inspector, 1L, "status",
                "narrative", "occupantSignatureUrl", "inspectorSignatureUrl", testFdid);
        InspectionRequest inspectionRequest = new InspectionRequest(inspection);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionRequest);
        when(inspectionService.createInspection(any(InspectionRequest.class)))
                .thenThrow(new IdNotNullException(errorMessage));

        MvcResult mvcResult = mockMvc.perform(post("/inspection/create")
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
    void createInspectionChildEntityDoesNotExistTest() throws Exception {
        String errorMessage = "id must be null for new Inspection";
        Inspector inspector = new Inspector(null, 1L, "first", "last", "2035559944", 1L);
        Inspection inspection = new Inspection(1L, 1L, inspector, 1L, "status",
                "narrative", "occupantSignatureUrl", "inspectorSignatureUrl", testFdid);
        InspectionRequest inspectionRequest = new InspectionRequest(inspection);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionRequest);
        when(inspectionService.createInspection(any(InspectionRequest.class)))
                .thenThrow(new EntityNotFoundException(errorMessage));

        MvcResult mvcResult = mockMvc.perform(post("/inspection/create")
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
    void createInspectionUnauthorizedTest() throws Exception {
        Inspector inspector = new Inspector(null, 1L, "first", "last", "2035559944", 1L);
        Inspection inspection = new Inspection(null, 1L, inspector, 1L, "status",
                "narrative", "occupantSignatureUrl", "inspectorSignatureUrl", testFdid);
        InspectionRequest inspectionRequest = new InspectionRequest(inspection);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionRequest);

        mockMvc.perform(post("/inspection/create")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)
        )
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void getInspectionTest() throws Exception {
        Inspector inspector = new Inspector(null, 1L, "first", "last", "2035559944", 1L);
        Inspection inspection = new Inspection(1L, 1L, inspector, 1L, "status",
                "narrative", "occupantSignatureUrl", "inspectorSignatureUrl", testFdid);
        InspectionResponse inspectionResponse = new InspectionResponse(inspection);
        when(inspectionService.findInspectionById(inspection.getInspectionId())).thenReturn(inspectionResponse);

        MvcResult mvcResult = mockMvc.perform(get("/inspection/{id}", inspection.getInspectionId()))
                .andExpect(status().isOk())
                .andReturn();

        String expectedBody = new ObjectMapper().writeValueAsString(inspectionResponse);
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(expectedBody, responseBody);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void getInspectionDoesNotExistTest() throws Exception {
        Long inspectionId = 1L;
        String errorMessage = "Inspection not found with id: " + inspectionId;
        when(inspectionService.findInspectionById(inspectionId)).thenThrow(new EntityNotFoundException(errorMessage));

        MvcResult mvcResult = mockMvc.perform(get("/inspection/{id}", inspectionId))
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
    void getInspectionUnauthorizedTest() throws Exception {
        Long inspectionId = 1L;

        mockMvc.perform(get("/inspection/{id}", inspectionId))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void updateInspectionTest() throws Exception {
        Inspector inspector = new Inspector(null, 1L, "first", "last", "2035559944", 1L);
        Inspection inspection = new Inspection(1L, 1L, inspector, 1L, "status",
                "narrative", "occupantSignatureUrl", "inspectorSignatureUrl", testFdid);
        InspectionRequest inspectionRequest = new InspectionRequest(inspection);
        InspectionResponse inspectionResponse = new InspectionResponse(inspection);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionRequest);
        when(inspectionService.updateInspection(any(InspectionRequest.class))).thenReturn(inspectionResponse);

        MvcResult mvcResult = mockMvc.perform(put("/inspection/update")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)
        )
                .andExpect(status().isOk())
                .andReturn();

        String expectedBody = new ObjectMapper().writeValueAsString(inspectionResponse);
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(expectedBody, responseBody);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void updateInspectionDoesNotExistTest() throws Exception {
        Inspector inspector = new Inspector(null, 1L, "first", "last", "2035559944", 1L);
        Inspection inspection = new Inspection(null, 1L, inspector, 1L, "status",
                "narrative", "occupantSignatureUrl", "inspectorSignatureUrl", testFdid);
        InspectionRequest inspectionRequest = new InspectionRequest(inspection);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionRequest);
        String errorMessage = "Inspection not found with id: " + inspection.getInspectionId();
        when(inspectionService.updateInspection(any(InspectionRequest.class)))
                .thenThrow(new EntityNotFoundException(errorMessage));

        MvcResult mvcResult = mockMvc.perform(put("/inspection/update")
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
    void updateInspectionChildEntityDoesNotExistTest() throws Exception {
        String errorMessage = "id must be null for new Inspection";
        Inspector inspector = new Inspector(null, 1L, "first", "last", "2035559944", 1L);
        Inspection inspection = new Inspection(1L, 1L, inspector, 1L, "status",
                "narrative", "occupantSignatureUrl", "inspectorSignatureUrl", testFdid);
        InspectionRequest inspectionRequest = new InspectionRequest(inspection);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionRequest);
        when(inspectionService.updateInspection(any(InspectionRequest.class)))
                .thenThrow(new EntityNotFoundException(errorMessage));

        MvcResult mvcResult = mockMvc.perform(put("/inspection/update")
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
    void updateInspectionUnauthorizedTest() throws Exception {
        Inspector inspector = new Inspector(null, 1L, "first", "last", "2035559944", 1L);
        Inspection inspection = new Inspection(null, 1L, inspector, 1L, "status",
                "narrative", "occupantSignatureUrl", "inspectorSignatureUrl", testFdid);
        InspectionRequest inspectionRequest = new InspectionRequest(inspection);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionRequest);

        mockMvc.perform(put("/inspection/update")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)
        )
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void deleteInspectionTest() throws Exception {
        Long inspectionId = 1L;

        mockMvc.perform(delete("/inspection/{id}", inspectionId))
                .andExpect(status().isNoContent())
                .andReturn();

        verify(inspectionService, times(1)).deleteInspection(inspectionId);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void deleteInspectionDoesNotExistTest() throws Exception {
        Long inspectionId = 1L;
        String errorMessage = "Inspection not found with id: " + inspectionId;
        doThrow(new EntityNotFoundException(errorMessage))
                .when(inspectionService).deleteInspection(inspectionId);

        MvcResult mvcResult = mockMvc.perform(delete("/inspection/{id}", inspectionId))
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
    void deleteInspectionUnauthorizedTest() throws Exception {
        Long inspectionId = 1L;

        mockMvc.perform(delete("/inspection/{id}", inspectionId))
                .andExpect(status().isForbidden())
                .andReturn();
    }
}
