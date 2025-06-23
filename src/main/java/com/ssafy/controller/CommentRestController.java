package com.ssafy.controller;

import com.ssafy.dto.CommentDTO;
import com.ssafy.service.CommentService;

import io.micrometer.core.annotation.Timed;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST 방식의 Comment API 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/comments")
public class CommentRestController {

    private final CommentService commentService;

    public CommentRestController(CommentService commentService) {
        this.commentService = commentService;
    }

    /** 특정 게시글의 댓글 전체 조회 (GET /api/v1/comments/post/{postId}) */
    @Timed(
    	    value       = "comments.getByPost",
    	    description = "특정 게시글의 댓글 조회에 소요된 시간",
    	    histogram   = true,
    	    percentiles = {0.5, 0.95}
    	)
    @GetMapping("/post/{postId}")
    public ResponseEntity<?> getCommentsByPost(@PathVariable int postId) {
        try {
            List<CommentDTO> comments = commentService.getCommentsByPost(postId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 특정 게시글의 좋아요 상위 3개 댓글 조회 (GET /api/v1/comments/post/{postId}/top3) */
    @GetMapping("/post/{postId}/top3")
    public ResponseEntity<?> getTop3CommentsByLikes(@PathVariable int postId) {
        try {
            List<CommentDTO> topComments = commentService.getTop3CommentsByLikes(postId);
            return ResponseEntity.ok(topComments);
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 단일 댓글 조회 (GET /api/v1/comments/{commentId}) */
    @GetMapping("/{commentId}")
    public ResponseEntity<?> getComment(@PathVariable int commentId) {
        try {
            CommentDTO comment = commentService.getCommentById(commentId);
            if (comment == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", "FAIL", "error", "Comment not found"));
            }
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 댓글 등록 (POST /api/v1/comments) */
    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CommentDTO payload) {
        try {
            commentService.createComment(payload);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("status", "SUCCESS", "comment", payload));
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 댓글 수정 (PUT /api/v1/comments/{commentId}) */
    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable int commentId,
                                           @RequestBody CommentDTO payload) {
        try {
            payload.setCommentId(commentId);
            commentService.updateComment(payload);
            CommentDTO updated = commentService.getCommentById(commentId);
            return ResponseEntity.ok(Map.of("status", "SUCCESS", "comment", updated));
        } catch (DataAccessException dae) {
        	dae.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "FAIL", "error", dae.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 댓글 삭제 (DELETE /api/v1/comments/{commentId}) */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable int commentId) {
        try {
            commentService.deleteComment(commentId);
            return ResponseEntity.ok(Map.of("status", "SUCCESS"));
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 댓글 좋아요 증가 (POST /api/v1/comments/{commentId}/like) */
    @PostMapping("/{commentId}/like")
    public ResponseEntity<?> likeComment(
            @PathVariable int commentId,
            @RequestParam("userId") int userId) {
        try {
            commentService.incrementCommentLikes(commentId, userId);
            return ResponseEntity.ok(Map.of("status", "SUCCESS"));
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 댓글 좋아요 감소 (DELETE /api/v1/comments/{commentId}/like) */
    @DeleteMapping("/{commentId}/like")
    public ResponseEntity<?> dislikeComment(
            @PathVariable int commentId,
            @RequestParam("userId") int userId) {
        try {
            commentService.decrementCommentLikes(commentId, userId);
            return ResponseEntity.ok(Map.of("status", "SUCCESS"));
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 댓글 좋아요 유무 확인 (GET /api/v1/comments/{commentId}/isliked) */
    @GetMapping("/{commentId}/isliked")
    public ResponseEntity<?> isCommentLiked(
            @PathVariable int commentId,
            @RequestParam("userId") int userId) {
        try {
            boolean isLiked = commentService.isCommentLiked(commentId, userId);
            return ResponseEntity.ok(Map.of("status", "SUCCESS", "isLiked", isLiked));
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }
}
