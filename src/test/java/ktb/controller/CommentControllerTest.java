package ktb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ktb.auth.filter.JwtAuthenticationFilter;
import ktb.config.TestSecurityConfig;
import ktb.config.WithMockCustomUser;
import ktb.dto.CommentDto;
import ktb.dto.request.CommentDeleteRequest;
import ktb.dto.request.CommentRequest;
import ktb.dto.request.CommentUpdateRequest;
import ktb.exception.AuthorizationException;
import ktb.service.CommentCommandService;
import ktb.service.CommentQueryService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = CommentController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@Import(TestSecurityConfig.class)
@DisplayName("CommentController 테스트")
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentQueryService commentQueryService;

    @MockBean
    private CommentCommandService commentCommandService;

    @Test
    @DisplayName("댓글 등록")
    @WithMockCustomUser(userId = 1L, email = "test@test.com", nickname = "testUser")
    void 댓글_등록_성공() throws Exception {
        // Given
        Long articleId = 1L;
        CommentRequest request = new CommentRequest("댓글 내용입니다");
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .articleId(articleId)
                .content("댓글 내용입니다")
                .createBy(1L)
                .createNickName("testUser")
                .build();

        when(commentCommandService.addComment(any(CommentDto.class))).thenReturn(commentDto);

        // When & Then
        mockMvc.perform(post("/api/v1/article/{id}/comments", articleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.createBy").value(1L))
                .andExpect(jsonPath("$.data.content").value("댓글 내용입니다"));

        verify(commentCommandService).addComment(any(CommentDto.class));
    }

    @Test
    @DisplayName("내용 없으면 댓글 등록 불가")
    @WithMockCustomUser
    void 댓글_등록_실패_내용_없음() throws Exception {
        // Given
        Long articleId = 1L;
        CommentRequest request = new CommentRequest("");

        // When & Then
        mockMvc.perform(post("/api/v1/article/{id}/comments", articleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("댓글 수정")
    @WithMockCustomUser(userId = 1L, email = "test@test.com", nickname = "testUser")
    void 댓글_수정_성공() throws Exception {
        // Given
        Long articleId = 1L;
        CommentUpdateRequest request = new CommentUpdateRequest(1L, "수정된 댓글 내용");
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .articleId(articleId)
                .content("수정된 댓글 내용")
                .createBy(1L)
                .createNickName("testUser")
                .build();

        when(commentCommandService.save(any(CommentDto.class))).thenReturn(commentDto);

        // When & Then
        mockMvc.perform(put("/api/v1/article/{id}/comments", articleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(commentCommandService).save(any(CommentDto.class));
    }

    @Test
    @DisplayName("내용 없으면 댓글 수정 불가")
    @WithMockCustomUser
    void 댓글_수정_실패_내용_없음() throws Exception {
        // Given
        Long articleId = 1L;
        CommentUpdateRequest request = new CommentUpdateRequest(1L, "");

        // When & Then
        mockMvc.perform(put("/api/v1/article/{id}/comments", articleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("댓글 ID 없으면 수정 불가")
    @WithMockCustomUser
    void 댓글_수정_실패_댓글ID_없음() throws Exception {
        // Given
        Long articleId = 1L;
        CommentUpdateRequest request = new CommentUpdateRequest(null, "수정된 댓글 내용");

        // When & Then
        mockMvc.perform(put("/api/v1/article/{id}/comments", articleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("권한 없으면 댓글 수정 불가")
    @WithMockCustomUser(userId = 2L, email = "other@test.com", nickname = "otherUser")
    void 댓글_수정_실패_권한_없음() throws Exception {
        // Given
        Long articleId = 1L;
        CommentUpdateRequest request = new CommentUpdateRequest(1L, "수정된 댓글 내용");

        when(commentCommandService.save(any(CommentDto.class)))
                .thenThrow(new AuthorizationException());

        // When & Then
        mockMvc.perform(put("/api/v1/article/{id}/comments", articleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(commentCommandService).save(any(CommentDto.class));
    }

    @Test
    @DisplayName("댓글 삭제")
    @WithMockCustomUser(userId = 1L)
    void 댓글_삭제_성공() throws Exception {
        // Given
        Long articleId = 1L;
        CommentDeleteRequest request = new CommentDeleteRequest(1L);

        doNothing().when(commentCommandService).delete(any(CommentDto.class));

        // When & Then
        mockMvc.perform(delete("/api/v1/article/{id}/comments", articleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(commentCommandService).delete(any(CommentDto.class));
    }

    @Test
    @DisplayName("댓글 ID 없으면 삭제 불가")
    @WithMockCustomUser
    void 댓글_삭제_실패_댓글ID_없음() throws Exception {
        // Given
        Long articleId = 1L;
        CommentDeleteRequest request = new CommentDeleteRequest(null);

        // When & Then
        mockMvc.perform(delete("/api/v1/article/{id}/comments", articleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("권한 없으면 댓글 삭제 불가")
    @WithMockCustomUser(userId = 2L, email = "other@test.com", nickname = "otherUser")
    void 댓글_삭제_실패_권한_없음() throws Exception {
        // Given
        Long articleId = 1L;
        CommentDeleteRequest request = new CommentDeleteRequest(1L);

        doThrow(new AuthorizationException()).when(commentCommandService).delete(any(CommentDto.class));

        // When & Then
        mockMvc.perform(delete("/api/v1/article/{id}/comments", articleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(commentCommandService).delete(any(CommentDto.class));
    }
}
