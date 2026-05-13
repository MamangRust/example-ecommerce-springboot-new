package com.sanedge.ecommerce.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.models.category.Category;
import com.sanedge.ecommerce.repository.category.CategoryCommandRepository;
import com.sanedge.ecommerce.security.JwtProvider;
import com.sanedge.ecommerce.service.FileService;
import com.sanedge.ecommerce.service.FolderService;

public class CategoryControllerIntegrationTest extends BaseIntegrationTest {

    @MockitoBean
    private FolderService folderService;

    @MockitoBean
    private FileService fileService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private CategoryCommandRepository categoryCommandRepository;

    private String authToken;

    @BeforeEach
    void setupAuth() {
        this.authToken = jwtProvider.generateAccessToken(adminUser.getUsername());
        when(folderService.createFolder(any(), any())).thenReturn("static/category/test");
        when(fileService.createFileImage(any(), any())).thenReturn("static/category/test/img.png");
    }

    @Test
    void shouldFindAllCategoriesAndCreate() throws Exception {
        Category category = new Category();
        category.setName("Music Instruments");
        category.setSlugCategory("music-instruments");
        category.setDescription("Instruments");
        categoryCommandRepository.save(category);

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/api/category")
                .header("Authorization", "Bearer " + authToken)
                .param("search", "Music")
                .param("page", "1")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Test creation via multipart post
        MockMultipartFile file = new MockMultipartFile("imageCategory", "test.png", "image/png", "bytes".getBytes());

        mockMvc.perform(multipart("/api/category/create")
                .file(file)
                .header("Authorization", "Bearer " + authToken)
                .param("name", "Kitchenware")
                .param("description", "Cookware items")
                .param("slugCategory", "kitchenware")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
}
