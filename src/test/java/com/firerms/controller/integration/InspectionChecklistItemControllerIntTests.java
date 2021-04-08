package com.firerms.controller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firerms.controller.InspectionChecklistItemController;
import com.firerms.controller.InspectionChecklistItemController;
import com.firerms.entity.checklists.InspectionChecklistItem;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.IdNotNullException;
import com.firerms.multiTenancy.RequestInterceptor;
import com.firerms.multiTenancy.TenantContext;
import com.firerms.request.InspectionChecklistItemRequest;
import com.firerms.response.InspectionChecklistItemResponse;
import com.firerms.security.service.CustomUserDetailsService;
import com.firerms.security.util.JwtUtility;
import com.firerms.service.InspectionChecklistItemService;
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
@WebMvcTest(InspectionChecklistItemController.class)
public class InspectionChecklistItemControllerIntTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private RequestInterceptor requestInterceptor;

    @MockBean
    private InspectionChecklistItemService inspectionChecklistItemService;

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
    void createInspectionChecklistItemTest() throws Exception {
        InspectionChecklistItem inspectionChecklistItem = new InspectionChecklistItem(null, 1L, 1L, "description", testFdid);
        InspectionChecklistItemRequest inspectionChecklistItemRequest = new InspectionChecklistItemRequest(inspectionChecklistItem);
        InspectionChecklistItemResponse inspectionChecklistItemResponse = new InspectionChecklistItemResponse(inspectionChecklistItem);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionChecklistItemRequest);
        when(inspectionChecklistItemService.createInspectionChecklistItem(any(InspectionChecklistItemRequest.class))).thenReturn(inspectionChecklistItemResponse);

        MvcResult mvcResult = mockMvc.perform(post("/inspection/checklistitem/create")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)
        )
                .andExpect(status().isCreated())
                .andReturn();

        String expectedBody = new ObjectMapper().writeValueAsString(inspectionChecklistItemResponse);
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(expectedBody, responseBody);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void createInspectionChecklistItemWithNonNullIdTest() throws Exception {
        String errorMessage = "id must be null for new InspectionChecklistItem";
        InspectionChecklistItem inspectionChecklistItem = new InspectionChecklistItem(1L, 1L, 1L, "description", testFdid);
        InspectionChecklistItemRequest inspectionChecklistItemRequest = new InspectionChecklistItemRequest(inspectionChecklistItem);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionChecklistItemRequest);
        when(inspectionChecklistItemService.createInspectionChecklistItem(any(InspectionChecklistItemRequest.class)))
                .thenThrow(new IdNotNullException(errorMessage));

        MvcResult mvcResult = mockMvc.perform(post("/inspection/checklistitem/create")
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
    void createInspectionChecklistItemChildEntityDoesNotExistTest() throws Exception {
        InspectionChecklistItem inspectionChecklistItem = new InspectionChecklistItem(null, 1L, 1L, "description", testFdid);
        InspectionChecklistItemRequest inspectionChecklistItemRequest = new InspectionChecklistItemRequest(inspectionChecklistItem);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionChecklistItemRequest);
        String errorMessage = "Inspection Checklist not found with id: " + inspectionChecklistItem.getInspectionChecklistId();
        when(inspectionChecklistItemService.createInspectionChecklistItem(any(InspectionChecklistItemRequest.class)))
                .thenThrow(new EntityNotFoundException(errorMessage));

        MvcResult mvcResult = mockMvc.perform(post("/inspection/checklistitem/create")
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
    void createInspectionChecklistItemUnauthorizedTest() throws Exception {
        InspectionChecklistItem inspectionChecklistItem = new InspectionChecklistItem(null, 1L, 1L, "description", testFdid);
        InspectionChecklistItemRequest inspectionChecklistItemRequest = new InspectionChecklistItemRequest(inspectionChecklistItem);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionChecklistItemRequest);

        mockMvc.perform(post("/inspection/checklistitem/create")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)
        )
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void getInspectionChecklistItemTest() throws Exception {
        InspectionChecklistItem inspectionChecklistItem = new InspectionChecklistItem(1L, 1L, 1L, "description", testFdid);
        InspectionChecklistItemResponse inspectionChecklistItemResponse = new InspectionChecklistItemResponse(inspectionChecklistItem);
        when(inspectionChecklistItemService.findInspectionChecklistItemById(inspectionChecklistItem.getInspectionChecklistItemId())).thenReturn(inspectionChecklistItemResponse);

        MvcResult mvcResult = mockMvc.perform(get("/inspection/checklistitem/{id}", inspectionChecklistItem.getInspectionChecklistItemId()))
                .andExpect(status().isOk())
                .andReturn();

        String expectedBody = new ObjectMapper().writeValueAsString(inspectionChecklistItemResponse);
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(expectedBody, responseBody);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void getInspectionChecklistItemDoesNotExistTest() throws Exception {
        Long inspectionChecklistItemId = 1L;
        String errorMessage = "Fire Code not found with id: " + inspectionChecklistItemId;
        when(inspectionChecklistItemService.findInspectionChecklistItemById(inspectionChecklistItemId)).thenThrow(new EntityNotFoundException(errorMessage));

        MvcResult mvcResult = mockMvc.perform(get("/inspection/checklistitem/{id}", inspectionChecklistItemId))
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
    void getInspectionChecklistItemUnauthorizedTest() throws Exception {
        Long inspectionChecklistItemId = 1L;

        mockMvc.perform(get("/inspection/checklistitem/{id}", inspectionChecklistItemId))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void updateInspectionChecklistItemTest() throws Exception {
        InspectionChecklistItem inspectionChecklistItem = new InspectionChecklistItem(1L, 1L, 1L, "description", testFdid);
        InspectionChecklistItemRequest inspectionChecklistItemRequest = new InspectionChecklistItemRequest(inspectionChecklistItem);
        InspectionChecklistItemResponse inspectionChecklistItemResponse = new InspectionChecklistItemResponse(inspectionChecklistItem);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionChecklistItemRequest);
        when(inspectionChecklistItemService.updateInspectionChecklistItem(any(InspectionChecklistItemRequest.class))).thenReturn(inspectionChecklistItemResponse);

        MvcResult mvcResult = mockMvc.perform(put("/inspection/checklistitem/update")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)
        )
                .andExpect(status().isOk())
                .andReturn();

        String expectedBody = new ObjectMapper().writeValueAsString(inspectionChecklistItemResponse);
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(expectedBody, responseBody);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void updateInspectionChecklistItemDoesNotExistTest() throws Exception {
        InspectionChecklistItem inspectionChecklistItem = new InspectionChecklistItem(null, 1L, 1L, "description", testFdid);
        InspectionChecklistItemRequest inspectionChecklistItemRequest = new InspectionChecklistItemRequest(inspectionChecklistItem);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionChecklistItemRequest);
        String errorMessage = "Inspection Checklist Item not found with id: " + inspectionChecklistItem.getInspectionChecklistItemId();
        when(inspectionChecklistItemService.updateInspectionChecklistItem(any(InspectionChecklistItemRequest.class)))
                .thenThrow(new EntityNotFoundException(errorMessage));

        MvcResult mvcResult = mockMvc.perform(put("/inspection/checklistitem/update")
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
    void updateInspectionChecklistItemChildEntityDoesNotExistTest() throws Exception {
        InspectionChecklistItem inspectionChecklistItem = new InspectionChecklistItem(null, 1L, 1L, "description", testFdid);
        InspectionChecklistItemRequest inspectionChecklistItemRequest = new InspectionChecklistItemRequest(inspectionChecklistItem);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionChecklistItemRequest);
        String errorMessage = "Fire Code not found with id: " + inspectionChecklistItem.getFireCodeId();
        when(inspectionChecklistItemService.updateInspectionChecklistItem(any(InspectionChecklistItemRequest.class)))
                .thenThrow(new EntityNotFoundException(errorMessage));

        MvcResult mvcResult = mockMvc.perform(put("/inspection/checklistitem/update")
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
    void updateInspectionChecklistItemUnauthorizedTest() throws Exception {
        InspectionChecklistItem inspectionChecklistItem = new InspectionChecklistItem(null, 1L, 1L, "description", testFdid);
        InspectionChecklistItemRequest inspectionChecklistItemRequest = new InspectionChecklistItemRequest(inspectionChecklistItem);
        String requestJson = new ObjectMapper().writeValueAsString(inspectionChecklistItemRequest);

        mockMvc.perform(put("/inspection/checklistitem/update")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson)
        )
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void deleteInspectionChecklistItemTest() throws Exception {
        Long inspectionChecklistItemId = 1L;

        mockMvc.perform(delete("/inspection/checklistitem/{id}", inspectionChecklistItemId))
                .andExpect(status().isNoContent())
                .andReturn();

        verify(inspectionChecklistItemService, times(1)).deleteInspectionChecklistItem(inspectionChecklistItemId);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void deleteInspectionChecklistItemDoesNotExistTest() throws Exception {
        Long inspectionChecklistItemId = 1L;
        String errorMessage = "Fire Code not found with id: " + inspectionChecklistItemId;
        doThrow(new EntityNotFoundException(errorMessage))
                .when(inspectionChecklistItemService).deleteInspectionChecklistItem(inspectionChecklistItemId);

        MvcResult mvcResult = mockMvc.perform(delete("/inspection/checklistitem/{id}", inspectionChecklistItemId))
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
    void deleteInspectionChecklistItemUnauthorizedTest() throws Exception {
        Long inspectionChecklistItemId = 1L;

        mockMvc.perform(delete("/inspectionChecklistItem/{id}", inspectionChecklistItemId))
                .andExpect(status().isForbidden())
                .andReturn();
    }
}
