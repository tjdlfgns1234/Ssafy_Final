package com.ssafy.dto;

import java.time.LocalDateTime;

/**
 * post 뷰 DTO
 */
public class PostDTO {
    private Integer postId;       // post_id
    private String title;         // title
    private String content;       // content
    private String tags;          // tags
    private LocalDateTime created;// created
    private LocalDateTime updated;// updated
    private Integer id;           // member.mno
    private String authorId;      // member.id
    private String authorName;    // author_name
    private Integer likes;        // likes
    private Integer commentCount; // 댓글 개수

    public PostDTO() {}

    public PostDTO(Integer postId, String title, String content, String tags,
                   LocalDateTime created, LocalDateTime updated,
                   Integer id, String authorId, String authorName,
                   Integer likes, Integer commentCount) {
        this.postId       = postId;
        this.title        = title;
        this.content      = content;
        this.tags         = tags;
        this.created      = created;
        this.updated      = updated;
        this.id           = id;
        this.authorId     = authorId;
        this.authorName   = authorName;
        this.likes        = likes;
        this.commentCount = commentCount;
    }

    // getters & setters
    public Integer getPostId() {
        return postId;
    }
    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getTags() {
        return tags;
    }
    public void setTags(String tags) {
        this.tags = tags;
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

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
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

    public Integer getLikes() {
        return likes;
    }
    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getCommentCount() {
        return commentCount;
    }
    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    @Override
    public String toString() {
        return "PostDTO{" +
               "postId=" + postId +
               ", title='" + title + '\'' +
               ", content='" + content + '\'' +
               ", tags='" + tags + '\'' +
               ", created=" + created +
               ", updated=" + updated +
               ", id=" + id +
               ", authorId='" + authorId + '\'' +
               ", authorName='" + authorName + '\'' +
               ", likes=" + likes +
               ", commentCount=" + commentCount +
               '}';
    }
}