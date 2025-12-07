package ktb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ktb.auth.filter.JwtAuthenticationFilter;
import ktb.common.pagination.PageInfo;
import ktb.common.pagination.Slice;
import ktb.config.TestSecurityConfig;
import ktb.config.WithMockCustomUser;
import ktb.dto.PageInfoDto;
import ktb.dto.request.ArticlePatchRequest;
import ktb.dto.request.ArticleRequest;
import ktb.dto.response.ArticleDetailDto;
import ktb.dto.response.ArticleSimpleDto;
import ktb.exception.AuthorizationException;
import ktb.service.ArticleCommandService;
import ktb.service.ArticleLikeService;
import ktb.service.ArticleQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ArticleController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@Import(TestSecurityConfig.class)
@DisplayName("ArticleController 테스트")
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ArticleQueryService articleQueryService;

    @MockBean
    private ArticleCommandService articleCommandService;

    @MockBean
    private ArticleLikeService articleLikeService;

    @Test
    @DisplayName("게시글 목록 조회 성공")
    void 게시글_목록_조회_성공() throws Exception {
        // Given
        ArticleSimpleDto article1 = new ArticleSimpleDto(
                1L,
                "게시글 제목 1",
                "작성자1",
                10,
                5,
                100,
                LocalDateTime.now()
        );

        List<ArticleSimpleDto> articles = List.of(article1);
        PageInfo pageInfo = PageInfo.of(true, 1L);
        Slice<ArticleSimpleDto> slice = Slice.of(articles, pageInfo);

        when(articleQueryService.findByIdAndCursorPagination(any(PageInfoDto.class)))
                .thenReturn(slice);

        // When & Then
        mockMvc.perform(get("/api/v1/articles")
                        .param("after", "0")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].title").value("게시글 제목 1"))
                .andExpect(jsonPath("$.pageInfo.endCursor").value(1))
                .andExpect(jsonPath("$.pageInfo.hasNext").value(true));

        verify(articleQueryService).findByIdAndCursorPagination(any(PageInfoDto.class));
    }

    @Test
    @DisplayName("limit이 유효하지 않으면 목록 조회 실패")
    void 게시글_목록_조회_실패_limit_음수() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/articles")
                        .param("after", "0")
                        .param("limit", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("게시글 등록 성공")
    @WithMockCustomUser(userId = 1L)
    void 게시글_등록_성공() throws Exception {
        // Given
        ArticleRequest request = new ArticleRequest(
                "게시글 제목",
                "게시글 내용",
                "https://image.com/image.png"
        );

        doNothing().when(articleCommandService).save(any());

        // When & Then
        mockMvc.perform(post("/api/v1/article")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(articleCommandService).save(any());
    }

    @Test
    @DisplayName("제목 없으면 게시글 등록 실패")
    @WithMockCustomUser
    void 게시글_등록_실패_제목_없음() throws Exception {
        // Given
        ArticleRequest request = new ArticleRequest(
                null,
                "게시글 내용",
                "https://image.com/image.png"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/article")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("제목 길이가 초과되면 게시글 등록 실패")
    @WithMockCustomUser
    void 게시글_등록_실패_제목_길이_초과() throws Exception {
        // Given
        ArticleRequest request = new ArticleRequest(
                "게시글 제목입니다게시글 제목입니다게시글 제목입니다",  // 27자
                "게시글 내용",
                "https://image.com/image.png"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/article")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("내용 없으면 게시글 등록 실패")
    @WithMockCustomUser
    void 게시글_등록_실패_내용_없음() throws Exception {
        // Given
        ArticleRequest request = new ArticleRequest(
                "게시글 제목",
                null,
                "https://image.com/image.png"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/article")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("게시글 상세 조회 성공")
    @WithMockCustomUser(userId = 1L)
    void 게시글_상세_조회_성공() throws Exception {
        // Given
        Long articleId = 1L;
        ArticleDetailDto articleDetail = new ArticleDetailDto(
                "1",
                "게시글 제목",
                "게시글 내용",
                "작성자",
                "10",
                "5",
                "100",
                true,
                LocalDateTime.now(),
                true,
                Collections.emptyList()
        );

        when(articleQueryService.findByIdDetail(anyLong(), anyLong())).thenReturn(articleDetail);

        // When & Then
        mockMvc.perform(get("/api/v1/article/{id}", articleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.title").value("게시글 제목"))
                .andExpect(jsonPath("$.data.content").value("게시글 내용"))
                .andExpect(jsonPath("$.data.is_liked").value(true));

        verify(articleQueryService).findByIdDetail(articleId, 1L);
    }

    @Test
    @DisplayName("게시글 좋아요 토글 성공")
    @WithMockCustomUser(userId = 1L)
    void 게시글_좋아요_토글_성공() throws Exception {
        // Given
        Long articleId = 1L;

        when(articleLikeService.toggleLike(anyLong(), anyLong())).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/v1/article/{id}/like", articleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.liked").value(true));

        verify(articleLikeService).toggleLike(articleId, 1L);
    }

    @Test
    @DisplayName("게시글 수정 성공")
    @WithMockCustomUser(userId = 1L)
    void 게시글_수정_성공() throws Exception {
        // Given
        Long articleId = 1L;
        ArticlePatchRequest request = new ArticlePatchRequest(
                "수정된 제목",
                "수정된 내용",
                "https://image.com/new-image.png"
        );

        doNothing().when(articleCommandService).save(any());

        // When & Then
        mockMvc.perform(patch("/api/v1/article/{id}", articleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(articleCommandService).save(any());
    }

    @Test
    @DisplayName("제목 없으면 게시글 수정 실패")
    @WithMockCustomUser
    void 게시글_수정_실패_제목_없음() throws Exception {
        // Given
        Long articleId = 1L;
        ArticlePatchRequest request = new ArticlePatchRequest(
                null,
                "수정된 내용",
                "https://image.com/new-image.png"
        );

        // When & Then
        mockMvc.perform(patch("/api/v1/article/{id}", articleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("내용 없으면 게시글 수정 실패")
    @WithMockCustomUser
    void 게시글_수정_실패_내용_없음() throws Exception {
        // Given
        Long articleId = 1L;
        ArticlePatchRequest request = new ArticlePatchRequest(
                "수정된 제목",
                null,
                "https://image.com/new-image.png"
        );

        // When & Then
        mockMvc.perform(patch("/api/v1/article/{id}", articleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    @WithMockCustomUser(userId = 1L)
    void 게시글_삭제_성공() throws Exception {
        // Given
        Long articleId = 1L;

        doNothing().when(articleCommandService).delete(anyLong(), anyLong());

        // When & Then
        mockMvc.perform(delete("/api/v1/article/{id}", articleId))
                .andExpect(status().isNoContent());

        verify(articleCommandService).delete(1L, articleId);
    }

    @Test
    @DisplayName("권한 없으면 게시글 수정 실패")
    @WithMockCustomUser(userId = 2L)
    void 게시글_수정_실패_권한_없음() throws Exception {
        // Given
        Long articleId = 1L;
        ArticlePatchRequest request = new ArticlePatchRequest(
                "수정된 제목",
                "수정된 내용",
                "https://image.com/new-image.png"
        );

        doThrow(new AuthorizationException()).when(articleCommandService).save(any());

        // When & Then
        mockMvc.perform(patch("/api/v1/article/{id}", articleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("해당 요청에 대한 접근 권한이 없습니다."));

        verify(articleCommandService).save(any());
    }

    @Test
    @DisplayName("권한 없으면 게시글 삭제 실패")
    @WithMockCustomUser(userId = 2L)
    void 게시글_삭제_실패_권한_없음() throws Exception {
        // Given
        Long articleId = 1L;

        doThrow(new AuthorizationException()).when(articleCommandService).delete(anyLong(), anyLong());

        // When & Then
        mockMvc.perform(delete("/api/v1/article/{id}", articleId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("해당 요청에 대한 접근 권한이 없습니다."));

        verify(articleCommandService).delete(2L, articleId);
    }
}
