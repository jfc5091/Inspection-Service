package com.firerms.controller.integration;

import com.amazonaws.AmazonServiceException;
import com.firerms.controller.InspectionViolationImageController;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.multiTenancy.RequestInterceptor;
import com.firerms.multiTenancy.TenantContext;
import com.firerms.security.service.CustomUserDetailsService;
import com.firerms.security.util.JwtUtility;
import com.firerms.service.InspectionViolationImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("local")
@AutoConfigureMockMvc
@WebMvcTest(InspectionViolationImageController.class)
public class InspectionViolationImageControllerIntTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private RequestInterceptor requestInterceptor;

    @MockBean
    private InspectionViolationImageService inspectionViolationImageService;

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
    void addInspectionViolationImageTest() throws Exception {
        Long inspectionViolationId = 1L;
        FileInputStream fis = new FileInputStream("src/test/java/helpers/images/inspection-violation-test-image.jpg");
        MockMultipartFile image = new MockMultipartFile("image", fis);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/inspection/violation/{id}/image/add", inspectionViolationId)
                .file(image)
        )
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void addInspectionViolationNullImageTest() throws Exception {
        String errorMessage = "Required request part 'image' is not present";
        Long inspectionViolationId = 1L;

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/inspection/violation/{id}/image/add", inspectionViolationId)        )
                .andExpect(status().isBadRequest())
                .andReturn();

        assertTrue(Objects.requireNonNull(mvcResult.getResponse().getErrorMessage()).contains(errorMessage));
        assertEquals(mvcResult.getResponse().getStatus(), 400);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void addInspectionViolationImageInspectionViolationDoesNotExistTest() throws Exception {
        Long inspectionViolationId = 1L;
        String errorMessage = "Inspection Violation not found with id: " + inspectionViolationId;
        FileInputStream fis = new FileInputStream("src/test/java/helpers/images/inspection-violation-test-image.jpg");
        MockMultipartFile image = new MockMultipartFile("image", fis);
        doThrow(new EntityNotFoundException(errorMessage))
                .when(inspectionViolationImageService).addInspectionViolationImage(eq(image), eq(inspectionViolationId));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/inspection/violation/{id}/image/add", inspectionViolationId)
                .file(image)
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
    void addInspectionViolationImageAmazonExceptionTest() throws Exception {
        Long inspectionViolationId = 1L;
        int statusCode = 502;
        FileInputStream fis = new FileInputStream("src/test/java/helpers/images/inspection-violation-test-image.jpg");
        MockMultipartFile image = new MockMultipartFile("image", fis);
        AmazonServiceException amazonServiceException = new AmazonServiceException("");
        amazonServiceException.setStatusCode(statusCode);
        doThrow(amazonServiceException)
                .when(inspectionViolationImageService).addInspectionViolationImage(eq(image), eq(inspectionViolationId));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/inspection/violation/{id}/image/add", inspectionViolationId)
                .file(image)
        )
                .andExpect(status().isBadGateway())
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString()
                .contains("\"status\":" + statusCode));
        assertTrue(mvcResult.getResponse().getContentAsString()
                .contains("\"timeStamp\":\""));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void deleteInspectionViolationImageTest() throws Exception {
        Long inspectionViolationId = 1L;
        String imageUrl = "imageUrl";

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/inspection/violation/{id}/image/delete", inspectionViolationId);
        builder.with(request -> {
            request.setMethod("DELETE");
            return request;
        });

        mockMvc.perform(builder
                .contentType(APPLICATION_JSON_UTF8)
                .content(imageUrl)
        )
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void deleteInspectionViolationNullImageUrlTest() throws Exception {
        Long inspectionViolationId = 1L;

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/inspection/violation/{id}/image/delete", inspectionViolationId);
        builder.with(request -> {
            request.setMethod("DELETE");
            return request;
        });

        mockMvc.perform(builder
                .contentType(APPLICATION_JSON_UTF8)
        )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "SUPER_ADMIN" })
    void deleteInspectionViolationImageInspectionViolationDoesNotExistTest() throws Exception {
        Long inspectionViolationId = 1L;
        String imageUrl = "imageUrl";
        String errorMessage = "Inspection Violation not found with id: " + inspectionViolationId;
        doThrow(new EntityNotFoundException(errorMessage))
                .when(inspectionViolationImageService).deleteInspectionViolationImage(eq(imageUrl), eq(inspectionViolationId));

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/inspection/violation/{id}/image/delete", inspectionViolationId);
        builder.with(request -> {
            request.setMethod("DELETE");
            return request;
        });

        MvcResult mvcResult = mockMvc.perform(builder
                .contentType(APPLICATION_JSON_UTF8)
                .content(imageUrl)
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
    void deleteInspectionViolationImageAmazonExceptionTest() throws Exception {
        Long inspectionViolationId = 1L;
        String imageUrl = "imageUrl";
        int statusCode = 502;
        AmazonServiceException amazonServiceException = new AmazonServiceException("");
        amazonServiceException.setStatusCode(statusCode);
        doThrow(amazonServiceException)
                .when(inspectionViolationImageService).deleteInspectionViolationImage(eq(imageUrl), eq(inspectionViolationId));

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/inspection/violation/{id}/image/delete", inspectionViolationId);
        builder.with(request -> {
            request.setMethod("DELETE");
            return request;
        });

        MvcResult mvcResult = mockMvc.perform(builder
                .contentType(APPLICATION_JSON_UTF8)
                .content(imageUrl)
        )
                .andExpect(status().isBadGateway())
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString()
                .contains("\"status\":" + statusCode));
        assertTrue(mvcResult.getResponse().getContentAsString()
                .contains("\"timeStamp\":\""));
    }
}
