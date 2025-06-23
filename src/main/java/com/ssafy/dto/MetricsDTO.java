package com.ssafy.dto;

public class MetricsDTO {
    private long memberCount;
    private long postsCount;
    private long commentsCount;
    private long postlikesCount;
    private long commentlikesCount;
    private long userplacesCount;
    private long travelplansCount;

    public MetricsDTO() {
    }

    public MetricsDTO(long memberCount, long postsCount, long commentsCount,
                          long postlikesCount, long commentlikesCount,
                          long userplacesCount, long travelplansCount) {
        this.memberCount       = memberCount;
        this.postsCount        = postsCount;
        this.commentsCount     = commentsCount;
        this.postlikesCount    = postlikesCount;
        this.commentlikesCount = commentlikesCount;
        this.userplacesCount   = userplacesCount;
        this.travelplansCount  = travelplansCount;
    }

    // getters
    public long getMemberCount()       { return memberCount; }
    public long getPostsCount()        { return postsCount; }
    public long getCommentsCount()     { return commentsCount; }
    public long getPostlikesCount()    { return postlikesCount; }
    public long getCommentlikesCount() { return commentlikesCount; }
    public long getUserplacesCount()   { return userplacesCount; }
    public long getTravelplansCount()  { return travelplansCount; }

    // setters
    public void setMemberCount(long memberCount)       { this.memberCount = memberCount; }
    public void setPostsCount(long postsCount)         { this.postsCount = postsCount; }
    public void setCommentsCount(long commentsCount)   { this.commentsCount = commentsCount; }
    public void setPostlikesCount(long postlikesCount) { this.postlikesCount = postlikesCount; }
    public void setCommentlikesCount(long commentlikesCount) { this.commentlikesCount = commentlikesCount; }
    public void setUserplacesCount(long userplacesCount)     { this.userplacesCount = userplacesCount; }
    public void setTravelplansCount(long travelplansCount)   { this.travelplansCount = travelplansCount; }

    @Override
    public String toString() {
        return "TableCountsDto{" +
               "memberCount=" + memberCount +
               ", postsCount=" + postsCount +
               ", commentsCount=" + commentsCount +
               ", postlikesCount=" + postlikesCount +
               ", commentlikesCount=" + commentlikesCount +
               ", userplacesCount=" + userplacesCount +
               ", travelplansCount=" + travelplansCount +
               '}';
    }
}