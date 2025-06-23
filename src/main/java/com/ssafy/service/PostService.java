package com.ssafy.service;

import com.ssafy.dto.PostDTO;
import java.util.List;

/**
 * 게시글 서비스 인터페이스
 */
public interface PostService {
    /** 전체 게시글 조회 */
    List<PostDTO> getAllPosts() throws Exception;

    /** 좋아요 상위 3개 게시글 조회 */
    List<PostDTO> getTop3Posts() throws Exception;

    /** 태그로 게시글 조회 */
    List<PostDTO> getPostsByTag(String tag) throws Exception;

    /** 단일 게시글 조회 */
    PostDTO getPostById(int postId) throws Exception;

    /** 게시글 등록 */
    void createPost(PostDTO post) throws Exception;

    /** 게시글 수정 */
    void updatePost(PostDTO post) throws Exception;

    /** 게시글 삭제 */
    void deletePost(int postId) throws Exception;

    /** 좋아요 증가 */
    void incrementLikes(int postId, int userId) throws Exception;

    /** 좋아요 감소 */
    void decrementLikes(int postId, int userId) throws Exception;

    /** 좋아요 유무 확인 */
    boolean isPostLiked(int postId, int userId) throws Exception;
}
