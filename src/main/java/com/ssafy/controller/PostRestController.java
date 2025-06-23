package com.ssafy.controller;

import com.ssafy.dto.PostDTO;
import com.ssafy.service.PostService;

import io.micrometer.core.annotation.Timed;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST 방식의 Post API 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/posts")
public class PostRestController {

    private final PostService service;

    public PostRestController(PostService service) {
        this.service = service;
    }

    /** 전체 게시글 조회 (GET /api/v1/posts) */
    @Timed(
    		value      = "posts.listAll",
            description= "전체 게시글 조회 시 걸린 시간",
            histogram  = true,
            percentiles= {0.5, 0.95}
    		)
    @GetMapping
    public ResponseEntity<?> listAll() {
        try {
            List<PostDTO> all = service.getAllPosts();
            return ResponseEntity.ok(Map.of("status", "SUCCESS", "posts", all));
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 상위 3개 게시글 조회 (GET /api/v1/posts/top3) */
    @GetMapping("/top3")
    public ResponseEntity<?> listTop3() {
        try {
            List<PostDTO> top = service.getTop3Posts();
            return ResponseEntity.ok(Map.of("status", "SUCCESS", "posts", top));
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 단일 게시글 조회 (GET /api/v1/posts/{postId}) */
    @GetMapping("/{postId}")
    public ResponseEntity<?> getOne(@PathVariable int postId) {
        try {
            PostDTO post = service.getPostById(postId);
            if (post == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", "FAIL", "error", "Post not found"));
            }
            return ResponseEntity.ok(Map.of("status", "SUCCESS", "post", post));
        } catch (Exception e) {	
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 게시글 등록 (POST /api/v1/posts) */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody PostDTO payload) {
        try {
            service.createPost(payload);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("status", "SUCCESS", "post", payload));
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 게시글 수정 (PUT /api/v1/posts/{postId}) */
    @PutMapping("/{postId}")
    public ResponseEntity<?> update(@PathVariable int postId,
                                    @RequestBody PostDTO payload) {
        try {
            payload.setPostId(postId);
            service.updatePost(payload);
            PostDTO updated = service.getPostById(postId);
            return ResponseEntity
                    .ok(Map.of("status", "SUCCESS", "post", updated));
        } catch (DataAccessException dae) {
        	dae.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "FAIL", "error", dae.getMessage()));
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 게시글 삭제 (DELETE /api/v1/posts/{postId}) */
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> delete(@PathVariable int postId) {
        try {
            service.deletePost(postId);
            return ResponseEntity.ok(Map.of("status", "SUCCESS"));
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 좋아요 증가 (POST /api/v1/posts/{postId}/like) */
    @PostMapping("/{postId}/like")
    public ResponseEntity<?> like(@PathVariable int postId,
                                  @RequestParam int userId) {
        try {
            service.incrementLikes(postId, userId);
            return ResponseEntity.ok(Map.of("status", "SUCCESS"));
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 좋아요 감소 (DELETE /api/v1/posts/{postId}/like) */
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<?> dislike(@PathVariable int postId,
                                     @RequestParam int userId) {
        try {
            service.decrementLikes(postId, userId);
            return ResponseEntity.ok(Map.of("status", "SUCCESS"));
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 좋아요 유무 확인 (GET /api/v1/posts/{postId}/isliked) */
    @GetMapping("/{postId}/isliked")
    public ResponseEntity<?> isLiked(@PathVariable int postId,
                                     @RequestParam int userId) {
        try {
            boolean isLiked = service.isPostLiked(postId, userId);
            return ResponseEntity.ok(Map.of("status", "SUCCESS", "isLiked", isLiked));
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }
}
