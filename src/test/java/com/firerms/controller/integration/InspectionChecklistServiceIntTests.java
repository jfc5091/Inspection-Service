package com.firerms.controller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firerms.controller.InspectionChecklistController;
import com.firerms.entity.checklists.InspectionChecklist;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.IdNotNullException;
import com.firerms.multiTenancy.RequestInterceptor;
import com.firerms.multiTenancy.TenantContext;
import com.firerms.request.InspectionChecklistRequest;
import com.firerms.response.InspectionChecklistResponse;
import com.firerms.security.service.CustomUserDetailsService;
import com.firerms.security.util.JwtUtility;
import com.firerms.service.InspectionChecklistService;
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
@WebMvcTest(InspectionChecklistController.class)
public class InspectionChecklistServiceIntTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private RequestInterceptor requestInterceptor;

    @MockBean
    private InspectionChecklistService inspectionChecklistService;

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
    void createInspectionChecklistTest() throws Exception {
        InspectionChecklist inspectionChecklist = new InspectionChecklist(null, "type", true, testFdid);
        InspectionChecklistRequest inspectionChecklistRequest = new InspectionChecklistRequest(inspectionChecklist);
        InspectionChecklistResponse inspectionChecklistResponse = new InspectionChecklistResponse(inspectionChecklist);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionChecklistRequest);
        when(inspectionChecklistService.createInspectionChecklist(any(InspectionChecklistRequest.class))).thenReturn(inspectionChecklistResponse);

        MvcResult mvcResult = mockMvc.perform(post("/inspection/checklist/create")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)
        )
                .andExpect(status().isCreated())
                .andReturn();

        String expectedBody = new ObjectMapper().writeValueAsString(inspectionChecklistResponse);
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(expectedBody, responseBody);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void createInspectionChecklistWithNonNullIdTest() throws Exception {
        String errorMessage = "id must be null for new InspectionChecklist";
        InspectionChecklist inspectionChecklist = new InspectionChecklist(1L, "type", true, testFdid);
        InspectionChecklistRequest inspectionChecklistRequest = new InspectionChecklistRequest(inspectionChecklist);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionChecklistRequest);
        when(inspectionChecklistService.createInspectionChecklist(any(InspectionChecklistRequest.class)))
                .thenThrow(new IdNotNullException(errorMessage));

        MvcResult mvcResult = mockMvc.perform(post("/inspection/checklist/create")
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
    void createInspectionChecklistUnauthorizedTest() throws Exception {
        InspectionChecklist inspectionChecklist = new InspectionChecklist(null, "type", true, testFdid);
        InspectionChecklistRequest inspectionChecklistRequest = new InspectionChecklistRequest(inspectionChecklist);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionChecklistRequest);

        mockMvc.perform(post("/inspection/checklist/create")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)
        )
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void getInspectionChecklistTest() throws Exception {
        InspectionChecklist inspectionChecklist = new InspectionChecklist(1L, "type", true, testFdid);
        InspectionChecklistResponse inspectionChecklistResponse = new InspectionChecklistResponse(inspectionChecklist);
        when(inspectionChecklistService.findInspectionChecklistById(inspectionChecklist.getInspectionChecklistId())).thenReturn(inspectionChecklistResponse);

        MvcResult mvcResult = mockMvc.perform(get("/inspection/checklist/{id}", inspectionChecklist.getInspectionChecklistId()))
                .andExpect(status().isOk())
                .andReturn();

        String expectedBody = new ObjectMapper().writeValueAsString(inspectionChecklistResponse);
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(expectedBody, responseBody);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void getInspectionChecklistDoesNotExistTest() throws Exception {
        Long inspectionChecklistId = 1L;
        String errorMessage = "Inspection Checklist not found with id: " + inspectionChecklistId;
        when(inspectionChecklistService.findInspectionChecklistById(inspectionChecklistId)).thenThrow(new EntityNotFoundException(errorMessage));

        MvcResult mvcResult = mockMvc.perform(get("/inspection/checklist/{id}", inspectionChecklistId))
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
    void getInspectionChecklistUnauthorizedTest() throws Exception {
        Long inspectionChecklistId = 1L;

        mockMvc.perform(get("/inspection/checklist/{id}", inspectionChecklistId))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void updateInspectionChecklistTest() throws Exception {
        InspectionChecklist inspectionChecklist = new InspectionChecklist(1L, "type", true, testFdid);
        InspectionChecklistRequest inspectionChecklistRequest = new InspectionChecklistRequest(inspectionChecklist);
        InspectionChecklistResponse inspectionChecklistResponse = new InspectionChecklistResponse(inspectionChecklist);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionChecklistRequest);
        when(inspectionChecklistService.updateInspectionChecklist(any(InspectionChecklistRequest.class))).thenReturn(inspectionChecklistResponse);

        MvcResult mvcResult = mockMvc.perform(put("/inspection/checklist/update")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)
        )
                .andExpect(status().isOk())
                .andReturn();

        String expectedBody = new ObjectMapper().writeValueAsString(inspectionChecklistResponse);
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(expectedBody, responseBody);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void updateInspectionChecklistDoesNotExistTest() throws Exception {
        InspectionChecklist inspectionChecklist = new InspectionChecklist(null, "type", true, testFdid);
        InspectionChecklistRequest inspectionChecklistRequest = new InspectionChecklistRequest(inspectionChecklist);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionChecklistRequest);
        String errorMessage = "Inspection Checklist not found with id: " + inspectionChecklist.getInspectionChecklistId();
        when(inspectionChecklistService.updateInspectionChecklist(any(InspectionChecklistRequest.class)))
                .thenThrow(new EntityNotFoundException(errorMessage));

        MvcResult mvcResult = mockMvc.perform(put("/inspection/checklist/update")
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
    void updateInspectionChecklistUnauthorizedTest() throws Exception {
        InspectionChecklist inspectionChecklist = new InspectionChecklist(null, "type", true, testFdid);
        InspectionChecklistRequest inspectionChecklistRequest = new InspectionChecklistRequest(inspectionChecklist);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionChecklistRequest);

        mockMvc.perform(put("/inspection/checklist/update")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)
        )
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void deleteInspectionChecklistTest() throws Exception {
        Long inspectionChecklistId = 1L;

        mockMvc.perform(delete("/inspection/checklist/{id}", inspectionChecklistId))
                .andExpect(status().isNoContent())
                .andReturn();

        verify(inspectionChecklistService, times(1)).deleteInspectionChecklist(inspectionChecklistId);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void deleteInspectionChecklistDoesNotExistTest() throws Exception {
        Long inspectionChecklistId = 1L;
        String errorMessage = "Inspection Checklist not found with id: " + inspectionChecklistId;
        doThrow(new EntityNotFoundException(errorMessage))
                .when(inspectionChecklistService).deleteInspectionChecklist(inspectionChecklistId);

        MvcResult mvcResult = mockMvc.perform(delete("/inspection/checklist/{id}", inspectionChecklistId))
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
    void deleteInspectionChecklistUnauthorizedTest() throws Exception {
        Long inspectionChecklistId = 1L;

        mockMvc.perform(delete("/inspection/checklist/{id}", inspectionChecklistId))
                .andExpect(status().isForbidden())
                .andReturn();
    }
}
