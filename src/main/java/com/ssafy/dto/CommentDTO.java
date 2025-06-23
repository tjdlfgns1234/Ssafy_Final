package com.ssafy.dto;

import java.time.LocalDateTime;

public class CommentDTO {
    private Integer commentId;
    private Integer postId;        // 댓글이 속한 게시글 ID
    private Integer userId;        // m.mno AS id
    private String authorId;       // m.id AS author_id
    private String authorName;     // m.name AS author_name
    private String content;
    private Integer likes;         // COUNT(likes.comment_id)
    private LocalDateTime created;
    private LocalDateTime updated;

    public CommentDTO() {}

    public CommentDTO(Integer commentId,
                      Integer postId,
                      Integer userId,
                      String authorId,
                      String authorName,
                      String content,
                      Integer likes,
                      LocalDateTime created,
                      LocalDateTime updated) {
        this.commentId  = commentId;
        this.postId     = postId;
        this.userId     = userId;
        this.authorId   = authorId;
        this.authorName = authorName;
        this.content    = content;
        this.likes      = likes;
        this.created    = created;
        this.updated    = updated;
    }

    // getters & setters
    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    @Override
    public String toString() {
        return "CommentDTO{" +
               "commentId=" + commentId +
               ", postId=" + postId +
               ", userId=" + userId +
               ", authorId='" + authorId + '\'' +
               ", authorName='" + authorName + '\'' +
               ", content='" + content + '\'' +
               ", likes=" + likes +
               ", created=" + created +
               ", updated=" + updated +
               '}';
    }
}
