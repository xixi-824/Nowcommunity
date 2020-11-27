package com.nowcoder.nowcommunity.service;

import com.nowcoder.nowcommunity.dao.MessageMapper;
import com.nowcoder.nowcommunity.entity.Message;
import com.nowcoder.nowcommunity.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    /**
     * 查询当前用户的会话列表，针对每个会话只返回一条最新的私信
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<Message> selectConversation(int userId,int offset,int limit){
        return messageMapper.selectConversation(userId, offset, limit);
    }

    /**
     * 查询当前用户的会话数量
     * @param userId
     * @return
     */
    public int selectConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }

    /**
     * 查询某个会话的私信列表
     * @param conversationId
     * @param offset
     * @param limit
     * @return
     */
    public List<Message> selectLetters(String conversationId,int offset,int limit){
        return messageMapper.selectLetters(conversationId,offset,limit);
    }

    /**
     * 查询某个会话包含的私信数量
     * @param conversationId
     * @return
     */
    public int selectLetterCount(String conversationId){
        return messageMapper.selectLetterCount(conversationId);
    }

    /**
     * 查询未读私信的数量
     * @param userId
     * @param conversationId
     * @return
     */
    public int selectLetterUnreadCount(int userId,String conversationId){
        return messageMapper.selectLetterUnreadCount(userId,conversationId);
    }

    /**
     * 添加过滤后的消息
     * @param message
     * @return
     */
    public int addMessage(Message message){
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));

        return messageMapper.insertMessage(message);
    }

    public int readMessage(List<Integer> ids){
        return messageMapper.updateStatus(ids,1);
    }
}
