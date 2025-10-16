package ktb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import ktb.dto.CommentDto;
import ktb.dto.request.CommentDeleteRequest;
import ktb.dto.request.CommentRequest;
import ktb.dto.request.CommentUpdateRequest;
import ktb.dto.response.CommonResponse;
import ktb.service.CommentService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Comment", description = "댓글 리소스 관련 API")
@RestController
@RequestMapping(("/article"))
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @Operation(
            summary = "Comment insert",
            description = "댓글을 등록합니다.",
            tags = { "Article", "Comment" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = CommentRequest.class))),
            }
    )
    @PostMapping("/{id}/comments")
    public ResponseEntity<CommonResponse<CommentDto>> addComment(
            @Parameter(name = "id", description = "게시글 ID", required = true) @PathVariable Long id,
            @Valid @RequestBody CommentRequest request
    ) {
        CommentDto comment = CommentDto.of(id, request.content(), request.userAccountId());
        CommentDto added = commentService.addComment(comment);

        return ResponseEntity.ok(CommonResponse.of("", added));
    }

    @Operation(
            summary = "Comment update",
            description = "댓글을 수정합니다.",
            tags = { "Article", "Comment" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = CommentUpdateRequest.class))),
            }
    )
    @PutMapping("/{id}/comments")
    public ResponseEntity<Void> updateComment(
            @Parameter(name = "id", description = "게시글 ID", required = true) @PathVariable Long id,
            @Valid @RequestBody CommentUpdateRequest request
    ) {
        CommentDto comment = new CommentDto(request.commentId(), id, request.content(), request.userAccountId());

        commentService.update(comment);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Comment delete",
            description = "댓글을 삭제합니다.",
            tags = { "Article", "Comment" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = CommentDeleteRequest.class))),
            }
    )
    @DeleteMapping("/{id}/comments")
    public ResponseEntity<Void> deleteComment(
            @Parameter(name = "id", description = "게시글 ID", required = true) @PathVariable Long id,
            @Valid @RequestBody CommentDeleteRequest request
    ) {
        CommentDto comment = new CommentDto(request.commentId(), id, null, request.userAccountId());

        commentService.delete(comment);

        return ResponseEntity.noContent().build();
    }
}
