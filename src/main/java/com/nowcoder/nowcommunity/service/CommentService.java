package com.nowcoder.nowcommunity.service;

import com.nowcoder.nowcommunity.dao.CommentMapper;
import com.nowcoder.nowcommunity.dao.DiscussPostMapper;
import com.nowcoder.nowcommunity.entity.Comment;
import com.nowcoder.nowcommunity.util.CommunityConstant;
import com.nowcoder.nowcommunity.util.SensitiveFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService implements CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    /**
     * 查询每个帖子的分页的评论
     * @param entityType
     * @param entityId
     * @param offset
     * @param limit
     * @return
     */
    public List<Comment> findCommentsByEntity(int entityType,int entityId,int offset,int limit){
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    /**
     * 查询当前贴子的总评论数量
     * @param entityType
     * @param entityId
     * @return
     */
    public int findCommentCount(int entityType,int entityId){
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    /**
     * 添加评论：添加评论 + 更新评论的数量  事务统一提交即可
     * 默认是抛出运行时异常才会导致事务回滚，此处出现异常就导致事务回滚
     * Exception : RunTimeException 和 IOException SQLException
     * @param comment
     * @return
     */
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public int addComment(Comment comment){
        if(comment == null ||  StringUtils.isBlank(comment.getContent())){
            throw new IllegalArgumentException("评论参数不能为空!");
        }

        // 1、对评论内容进行过滤
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));

        // 2、插入评论
        int rows = commentMapper.insertComment(comment);
        if(rows < 1){
            throw new IllegalArgumentException("评论添加失败!");
        }

        // 3、只有当对帖子评论时，才修改评论数量
        // 3、修改帖子评论的数量
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            // 查询当前帖子评论的总数
            // 对帖子评论时,targetId = 0,entityId : 对应目标帖子编号  EntityType : 评论类型
            int count = commentMapper.selectCountByEntity(ENTITY_TYPE_POST, comment.getEntityId());
            discussPostMapper.updateCommentCount(comment.getUserId(),count);
        }

        return rows;
    }

}
