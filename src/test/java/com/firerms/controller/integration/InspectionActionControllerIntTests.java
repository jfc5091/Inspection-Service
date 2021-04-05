package com.firerms.controller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.firerms.controller.InspectionActionController;
import com.firerms.entity.inspections.InspectionAction;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.IdNotNullException;
import com.firerms.multiTenancy.RequestInterceptor;
import com.firerms.multiTenancy.TenantContext;
import com.firerms.request.InspectionActionRequest;
import com.firerms.response.InspectionActionResponse;
import com.firerms.security.service.CustomUserDetailsService;
import com.firerms.security.util.JwtUtility;
import com.firerms.service.InspectionActionService;
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
@WebMvcTest(InspectionActionController.class)
public class InspectionActionControllerIntTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private RequestInterceptor requestInterceptor;

    @MockBean
    private InspectionActionService inspectionActionService;

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
    void createInspectionActionTest() throws Exception {
        InspectionAction inspectionAction = new InspectionAction(null, 1L, "action", new Date(), "description", "narrative", testFdid);
        InspectionActionRequest inspectionActionRequest = new InspectionActionRequest(inspectionAction);
        InspectionActionResponse inspectionActionResponse = new InspectionActionResponse(inspectionAction);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionActionRequest);
        when(inspectionActionService.createInspectionAction(any(InspectionActionRequest.class))).thenReturn(inspectionActionResponse);

        MvcResult mvcResult = mockMvc.perform(post("/inspection/action/create")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)
        )
                .andExpect(status().isCreated())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String expectedBody = objectMapper.writeValueAsString(inspectionActionResponse);
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(expectedBody, responseBody);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void createInspectionActionWithNonNullIdTest() throws Exception {
        String errorMessage = "id must be null for new InspectionAction";
        InspectionAction inspectionAction = new InspectionAction(1L, 1L, "action", new Date(), "description", "narrative", testFdid);
        InspectionActionRequest inspectionActionRequest = new InspectionActionRequest(inspectionAction);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionActionRequest);
        when(inspectionActionService.createInspectionAction(any(InspectionActionRequest.class)))
                .thenThrow(new IdNotNullException(errorMessage));

        MvcResult mvcResult = mockMvc.perform(post("/inspection/action/create")
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
    void createInspectionActionUnauthorizedTest() throws Exception {
        InspectionAction inspectionAction = new InspectionAction(null, 1L, "action", new Date(), "description", "narrative", testFdid);
        InspectionActionRequest inspectionActionRequest = new InspectionActionRequest(inspectionAction);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionActionRequest);

        mockMvc.perform(post("/inspection/action/create")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)
        )
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void getInspectionActionTest() throws Exception {
        InspectionAction inspectionAction = new InspectionAction(1L, 1L, "action", new Date(), "description", "narrative", testFdid);
        InspectionActionResponse inspectionActionResponse = new InspectionActionResponse(inspectionAction);
        when(inspectionActionService.findInspectionActionById(inspectionAction.getInspectionActionId())).thenReturn(inspectionActionResponse);

        MvcResult mvcResult = mockMvc.perform(get("/inspection/action/{id}", inspectionAction.getInspectionActionId()))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String expectedBody = objectMapper.writeValueAsString(inspectionActionResponse);
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(expectedBody, responseBody);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void getInspectionActionDoesNotExistTest() throws Exception {
        Long inspectionActionId = 1L;
        String errorMessage = "Inspection Action not found with id: " + inspectionActionId;
        when(inspectionActionService.findInspectionActionById(inspectionActionId)).thenThrow(new EntityNotFoundException(errorMessage));

        MvcResult mvcResult = mockMvc.perform(get("/inspection/action/{id}", inspectionActionId))
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
    void getInspectionActionUnauthorizedTest() throws Exception {
        Long inspectionActionId = 1L;

        mockMvc.perform(get("/inspection/action/{id}", inspectionActionId))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void updateInspectionActionTest() throws Exception {
        InspectionAction inspectionAction = new InspectionAction(1L, 1L, "action", new Date(), "description", "narrative", testFdid);
        InspectionActionRequest inspectionActionRequest = new InspectionActionRequest(inspectionAction);
        InspectionActionResponse inspectionActionResponse = new InspectionActionResponse(inspectionAction);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionActionRequest);
        when(inspectionActionService.updateInspectionAction(any(InspectionActionRequest.class))).thenReturn(inspectionActionResponse);

        MvcResult mvcResult = mockMvc.perform(put("/inspection/action/update")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)
        )
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String expectedBody = objectMapper.writeValueAsString(inspectionActionResponse);
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(expectedBody, responseBody);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void updateInspectionActionDoesNotExistTest() throws Exception {
        InspectionAction inspectionAction = new InspectionAction(1L, 1L, "action", new Date(), "description", "narrative", testFdid);
        InspectionActionRequest inspectionActionRequest = new InspectionActionRequest(inspectionAction);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionActionRequest);
        String errorMessage = "Inspection Action not found with id: " + inspectionAction.getInspectionActionId();
        when(inspectionActionService.updateInspectionAction(any(InspectionActionRequest.class)))
                .thenThrow(new EntityNotFoundException(errorMessage));

        MvcResult mvcResult = mockMvc.perform(put("/inspection/action/update")
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
    void updateInspectionActionUnauthorizedTest() throws Exception {
        InspectionAction inspectionAction = new InspectionAction(1L, 1L, "action", new Date(), "description", "narrative", testFdid);
        InspectionActionRequest inspectionActionRequest = new InspectionActionRequest(inspectionAction);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionActionRequest);

        mockMvc.perform(put("/inspection/action/update")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)
        )
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void deleteInspectionActionTest() throws Exception {
        Long inspectionActionId = 1L;

        mockMvc.perform(delete("/inspection/action/{id}", inspectionActionId))
                .andExpect(status().isNoContent())
                .andReturn();

        verify(inspectionActionService, times(1)).deleteInspectionAction(inspectionActionId);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void deleteInspectionActionDoesNotExistTest() throws Exception {
        Long inspectionActionId = 1L;
        String errorMessage = "Inspection Action not found with id: " + inspectionActionId;
        doThrow(new EntityNotFoundException(errorMessage))
                .when(inspectionActionService).deleteInspectionAction(inspectionActionId);

        MvcResult mvcResult = mockMvc.perform(delete("/inspection/action/{id}", inspectionActionId))
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
    void deleteInspectionActionUnauthorizedTest() throws Exception {
        Long inspectionActionId = 1L;

        mockMvc.perform(delete("/inspectionAction/{id}", inspectionActionId))
                .andExpect(status().isForbidden())
                .andReturn();
    }
}
