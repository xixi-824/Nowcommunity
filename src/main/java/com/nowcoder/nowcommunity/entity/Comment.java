package com.nowcoder.nowcommunity.entity;

import java.util.Date;
import java.util.Objects;

public class Comment {
    //
    private int id;

    /**
     * 用户id
     */
    private int userId;
    /**
     * 评论：对帖子的评论
     * 回复：对评论的评论
     */
    private int entityType;
    /**
     * 发帖的id
     */
    private int entityId;
    /**
     * 回复对targetId发表的评论
     */
    private int targetId;
    private String content;
    private int status;
    private Date createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", userId=" + userId +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", targetId=" + targetId +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Comment comment = (Comment) o;
        return id == comment.id &&
                userId == comment.userId &&
                entityType == comment.entityType &&
                entityId == comment.entityId &&
                targetId == comment.targetId &&
                status == comment.status &&
                Objects.equals(content, comment.content) &&
                Objects.equals(createTime, comment.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, entityType, entityId, targetId, content, status, createTime);
    }
}
