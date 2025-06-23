package com.ssafy.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.dto.PostDTO;

@Mapper
public interface PostRepository {
    /** Create */
    int postInsert(PostDTO post) throws Exception;

    /** Read All */
    List<PostDTO> postSelectAll() throws Exception;

    /** Read Top 3 by likes */
    List<PostDTO> postSelectTop3() throws Exception;

    /** Read by tag */
    List<PostDTO> postSelectByTag(@Param("tag") String tag) throws Exception;

    /** Read One */
    PostDTO postSelect(@Param("postId") int postId) throws Exception;

    /** Update */
    int postUpdate(PostDTO post) throws Exception;

    /** Delete */
    int postDelete(@Param("postId") int postId) throws Exception;

    /** Likes 증가 */
    int incrementLikes(@Param("postId") int postId, @Param("userId") int userId) throws Exception;

    /** Likes 감소 */
    int decrementLikes(@Param("postId") int postId, @Param("userId") int userId) throws Exception;

    /** 좋아요 유무 확인 */
    int isPostLiked(@Param("postId") int postId, @Param("userId") int userId) throws Exception;
}
