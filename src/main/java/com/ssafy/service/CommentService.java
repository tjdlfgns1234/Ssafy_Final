package com.ssafy.service;

import com.ssafy.dto.CommentDTO;
import java.util.List;

/**
 * 댓글 서비스 인터페이스
 */
public interface CommentService {

    /** 댓글 등록 */
    void createComment(CommentDTO comment) throws Exception;

    /** 특정 게시글의 댓글 전체 조회 */
    List<CommentDTO> getCommentsByPost(int postId) throws Exception;

    /** 특정 게시글의 좋아요 상위 3개 댓글 조회 */
    List<CommentDTO> getTop3CommentsByLikes(int postId) throws Exception;

    /** 단일 댓글 조회 */
    CommentDTO getCommentById(int commentId) throws Exception;

    /** 댓글 수정 */
    void updateComment(CommentDTO comment) throws Exception;

    /** 댓글 삭제 */
    void deleteComment(int commentId) throws Exception;

    /** 댓글 좋아요 증가 */
    void incrementCommentLikes(int commentId, int userId) throws Exception;

    /** 댓글 좋아요 감소 */
    void decrementCommentLikes(int commentId, int userId) throws Exception;

    /** 댓글 좋아요 유무 확인 */
    boolean isCommentLiked(int commentId, int userId) throws Exception;
}
