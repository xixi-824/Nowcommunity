package com.nowcoder.nowcommunity.Controlller;

import com.nowcoder.nowcommunity.annotation.LoginRequired;
import com.nowcoder.nowcommunity.entity.Message;
import com.nowcoder.nowcommunity.entity.Page;
import com.nowcoder.nowcommunity.entity.User;
import com.nowcoder.nowcommunity.service.MessageService;
import com.nowcoder.nowcommunity.service.UserService;
import com.nowcoder.nowcommunity.util.CommunityUtil;
import com.nowcoder.nowcommunity.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    /**
     * 当前登录用户的会话列表查看
     * @param model
     * @param page
     * @return
     */
    @LoginRequired
    @RequestMapping(path = "/letter/list",method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){
        // 错误模拟
//        Integer.valueOf("abc");

        User user = hostHolder.getUser();
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.selectConversationCount(user.getId()));

        // 当前用户的会话列表
        List<Message> conversationList = messageService.
                selectConversation(user.getId(), page.getOffset(), page.getLimit());

        List<Map<String,Object>> conversations = new ArrayList<>(conversationList.size());
        // 遍历会话列表
        if(conversationList != null){
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>();
                // 会话的消息内容(最新的信息)
                map.put("conversation", message);
                // 每个会话的消息总数
                map.put("letterCount", messageService.selectLetterCount(message.getConversationId()));
                // 每个会话消息的总数
                map.put("UnreadCount", messageService.selectLetterUnreadCount(user.getId(), message.getConversationId()));

                // 显示与当前用户对话的私信用户信息
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));

                // 单个会话对象内容放入集合中
                conversations.add(map);
            }
        }
        model.addAttribute("conversations",conversations);

        // 查询当前登录用户所有会话未读消息总量
        model.addAttribute("totalUnreadCount",messageService.selectLetterUnreadCount(user.getId(),null));

        return "/site/letter";
    }

    /**
     * 单个会话的具体内容
     * @param conversationId
     * @param page
     * @param model
     * @return
     */
    @LoginRequired
    @RequestMapping(path = "/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,Page page,Model model){
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        // 单个会话对象的对话总数量
        page.setRows(messageService.selectLetterCount(conversationId));

        // 私信列表
        List<Message> letterList = messageService.selectLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String,Object>> letters = new ArrayList<>();
        if(letterList != null){
            for (Message message : letterList) {
                Map<String,Object> map = new HashMap<>();
                map.put("message",message);
                // 显示发信人的头像
                map.put("fromUser",userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }

        model.addAttribute("letters",letters);
        // 对话目标用户的私信用户信息
        model.addAttribute("target",getLetterTarget(conversationId,hostHolder.getUser().getId()));

        // 设置为已读
        List<Integer> ids = getLetterIds(letterList);
        if(!ids.isEmpty()){
            // 未读消息设置为已读
            messageService.readMessage(ids);
        }
        return "/site/letter-detail";
    }

    private List<Integer> getLetterIds(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();
        int userId = hostHolder.getUser().getId();
        if(letterList != null){
            for (Message message : letterList) {
                // 当前登录用户是私信的接收方，才能标记未读消息为已读
                if(userId == message.getToId() && message.getStatus() == 0){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    /**
     * 获取登录用户私信的对话用户信息
     * @param conversationId
     * @param userId
     * @return
     */
    private User getLetterTarget(String conversationId,int userId){
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        int targetId = userId == id0 ? id1 : id0;
        return userService.findUserById(targetId);
    }


    /**
     * 异步请求发送私信消息
     * @param toName
     * @param content
     * @return
     */
    @RequestMapping(path = "/letter/send",method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName,String content){
//        Integer.valueOf("abc");

        // 查询当前用户是否登录
        User user = hostHolder.getUser();
        if(user == null){
            return CommunityUtil.getJSONString(2,"用户未登录，无法发送私信");
        }

        // 私信内容不能为空
        if(content == null || StringUtils.isBlank(content)){
            return CommunityUtil.getJSONString(4,"私信内容不能为空");
        }

        // 查询发送私信的对方用户信息
        User target = userService.findUserByName(toName);
        if(target == null){
            return CommunityUtil.getJSONString(1,"私信用户不存在");
        }



        //

        // 设置消息对象内容
        Message message = new Message();
        message.setFromId(user.getId());
        message.setToId(target.getId());
        if(message.getFromId() < message.getToId()){
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        }else if (message.getFromId() > message.getToId()){
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }else{
            // fromId = toId
            // 登录用户不能给自己发私信
            return CommunityUtil.getJSONString(3,"只能给其他人发私信哦");
        }

        message.setContent(content);
        message.setCreateTime(new Date());
        // 添加私信
        messageService.addMessage(message);
        return CommunityUtil.getJSONString(0,"私信发送成功");

    }

}
