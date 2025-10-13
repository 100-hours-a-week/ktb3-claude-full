package ktb.controller;

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
import org.springframework.web.bind.annotation.RestController;

@RestController("/article")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommonResponse<CommentDto>> addComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest request
    ) {
        CommentDto comment = CommentDto.of(id, request.content(), request.userAccountId());
        CommentDto added = commentService.addComment(comment);

        return ResponseEntity.ok(CommonResponse.of("", added));
    }

    @PutMapping("/{id}/comments")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentUpdateRequest request
    ) {
        CommentDto comment = new CommentDto(request.commentId(), id, request.content(), request.userAccountId());

        commentService.update(comment);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/comments")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentDeleteRequest request
    ) {
        CommentDto comment = new CommentDto(request.commentId(), id, null, request.userAccountId());

        commentService.delete(comment);

        return ResponseEntity.noContent().build();
    }
}
