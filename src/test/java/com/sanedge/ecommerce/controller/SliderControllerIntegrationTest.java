package com.sanedge.ecommerce.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.security.JwtProvider;
import com.sanedge.ecommerce.service.FileService;
import com.sanedge.ecommerce.service.FolderService;

public class SliderControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @MockitoBean
    private FolderService folderService;

    @MockitoBean
    private FileService fileService;

    private String authToken;

    @BeforeEach
    void setupAuth() {
        this.authToken = jwtProvider.generateAccessToken(adminUser.getUsername());
    }

    @Test
    void shouldFindAllAndCreateSlider() throws Exception {
        when(folderService.createFolder(any(), any())).thenReturn("mock-folder");
        when(fileService.createFileImage(any(), any())).thenReturn("mock-slider.jpg");

        mockMvc.perform(get("/api/slider")
                .header("Authorization", "Bearer " + authToken)
                .param("search", "welcome")
                .param("page", "1")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        MockMultipartFile file = new MockMultipartFile("filePath", "welcome.jpg", MediaType.IMAGE_JPEG_VALUE, "image".getBytes());

        mockMvc.perform(multipart("/api/slider/create")
                .file(file)
                .header("Authorization", "Bearer " + authToken)
                .param("nama", "Controller Slider")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
}
