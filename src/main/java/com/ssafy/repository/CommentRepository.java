package com.ssafy.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.dto.CommentDTO;

@Mapper
public interface CommentRepository {
    /** Create */
    int CommentInsert(CommentDTO comment) throws Exception;

    /** Read All by Post */
    List<CommentDTO> CommentSelectAllByPost(@Param("postId") int postId) throws Exception;

    /** Read Top 3 by likes for a Post */
    List<CommentDTO> CommentSelectTop3ByLikes(@Param("postId") int postId) throws Exception;

    /** Read One */
    CommentDTO CommentSelect(@Param("commentId") int commentId) throws Exception;

    /** Update content and likes */
    int CommentUpdate(CommentDTO comment) throws Exception;

    /** Delete */
    int CommentDelete(@Param("commentId") int commentId) throws Exception;

    /** Likes 증가 */
    int incrementLikes(
        @Param("commentId") int commentId,
        @Param("userId") int userId
    ) throws Exception;

    /** Likes 감소 */
    int decrementLikes(
        @Param("commentId") int commentId,
        @Param("userId") int userId
    ) throws Exception;

    /** like 유무 확인 */
    int isCommentLiked(
        @Param("commentId") int commentId,
        @Param("userId") int userId
    ) throws Exception;
}
