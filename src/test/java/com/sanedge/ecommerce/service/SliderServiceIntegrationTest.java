package com.sanedge.ecommerce.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.domain.requests.slider.CreateSliderRequest;
import com.sanedge.ecommerce.domain.requests.slider.FindAllSliderRequest;
import com.sanedge.ecommerce.domain.requests.slider.UpdateSliderRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.slider.SliderResponse;
import com.sanedge.ecommerce.domain.responses.slider.SliderResponseDeleteAt;
import com.sanedge.ecommerce.service.slider.SliderCommandService;
import com.sanedge.ecommerce.service.slider.SliderQueryService;

import jakarta.validation.Validator;

public class SliderServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private SliderCommandService commandService;

    @Autowired
    private SliderQueryService queryService;

    @MockBean
    private FolderService folderService;

    @MockBean
    private FileService fileService;

    @MockBean
    private Validator validator;

    @BeforeEach
    void setupMock() {
        when(folderService.createFolder(any(), any())).thenReturn("mock-folder");
        when(fileService.createFileImage(any(), any())).thenReturn("mock-slider.jpg");
    }

    @Test
    void testAllSliderServiceMethods() {
        MockMultipartFile file = new MockMultipartFile("filePath", "welcome.jpg", "image/jpeg", "image".getBytes());

        // 1. Create
        CreateSliderRequest req = new CreateSliderRequest();
        req.setNama("UniqueSlider");
        req.setFilePath(file);

        ApiResponse<SliderResponse> createResp = commandService.createSlider(req);
        assertThat(createResp.getStatus()).isEqualTo("success");
        Long id = createResp.getData().getId();

        entityManager.flush();
        entityManager.clear();

        // 2. Find All
        FindAllSliderRequest findReq = new FindAllSliderRequest();
        findReq.setSearch("UniqueSlider");
        findReq.setPage(1);
        findReq.setPageSize(10);

        ApiResponsePagination<List<SliderResponse>> listResp = queryService.findAll(findReq);
        assertThat(listResp.getStatus()).isEqualTo("success");

        // 3. Update
        UpdateSliderRequest updateReq = new UpdateSliderRequest();
        updateReq.setId(id.intValue());
        updateReq.setNama("UpdatedSlider");
        updateReq.setFilePath(file);

        ApiResponse<SliderResponse> updateResp = commandService.updateSlider(updateReq);
        assertThat(updateResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // 4. Find By Active
        ApiResponsePagination<List<SliderResponseDeleteAt>> activeResp = queryService.findByActive(findReq);
        assertThat(activeResp.getStatus()).isEqualTo("success");

        // 5. Trash
        ApiResponse<SliderResponseDeleteAt> trashResp = commandService.trashedSlider(id.intValue());
        assertThat(trashResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // 6. Find By Trashed
        ApiResponsePagination<List<SliderResponseDeleteAt>> trashedResp = queryService.findByTrashed(findReq);
        assertThat(trashedResp.getStatus()).isEqualTo("success");

        // 7. Restore
        ApiResponse<SliderResponseDeleteAt> restoreResp = commandService.restoreSlider(id.intValue());
        assertThat(restoreResp.getStatus()).isEqualTo("success");

        // Trash again before delete permanent
        ApiResponse<SliderResponseDeleteAt> trashAgainResp = commandService.trashedSlider(id.intValue());
        assertThat(trashAgainResp.getStatus()).isEqualTo("success");

        // 8. Delete Permanent
        ApiResponse<Boolean> delPermResp = commandService.deleteSliderPermanent(id.intValue());
        assertThat(delPermResp.getStatus()).isEqualTo("success");

        // 9. Restore All
        ApiResponse<Boolean> restoreAllResp = commandService.restoreAllSliders();
        assertThat(restoreAllResp.getStatus()).isEqualTo("success");

        // 10. Delete All Permanent
        ApiResponse<Boolean> delAllResp = commandService.deleteAllSlidersPermanent();
        assertThat(delAllResp.getStatus()).isEqualTo("success");
    }
}
