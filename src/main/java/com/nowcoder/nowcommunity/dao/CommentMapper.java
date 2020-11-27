package com.nowcoder.nowcommunity.dao;

import com.nowcoder.nowcommunity.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CommentMapper {

    /**
     * 查询每个帖子的分页的评论
     * @param entityType
     * @param entityId
     * @param offset
     * @param limit
     * @return
     */
    List<Comment> selectCommentsByEntity(int entityType,int entityId,int offset,int limit);

    /**
     * 查询当前贴子的总评论数量
     * @param entityType
     * @param entityId
     * @return
     */
    int selectCountByEntity(int entityType,int entityId);

    /**
     * 插入评论
     * @param comment
     * @return
     */
    int insertComment(Comment comment);

}
