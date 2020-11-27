package com.nowcoder.nowcommunity.Controlller;

import com.nowcoder.nowcommunity.annotation.LoginRequired;
import com.nowcoder.nowcommunity.entity.Comment;
import com.nowcoder.nowcommunity.entity.User;
import com.nowcoder.nowcommunity.service.CommentService;
import com.nowcoder.nowcommunity.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping(path="/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @RequestMapping(path = "/add/{discussPostId}",method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment, Model model){
        // 当前登录用户才能评论
        User user = hostHolder.getUser();
        // 1、设置评论的信息
        comment.setUserId(user.getId());
        comment.setCreateTime(new Date());
        comment.setStatus(0);

        if(comment == null || StringUtils.isBlank(comment.getContent())){
            model.addAttribute("commentError","评论内容不能为空哦!");
            return "redirect:/discuss/detail/" + discussPostId;
        }

        commentService.addComment(comment);
        // 重定向查询帖子评论详情，防止用户F5刷新整个页面(重复之前的请求操作)，导致重复评论
        return "redirect:/discuss/detail/" + discussPostId;
    }

}
