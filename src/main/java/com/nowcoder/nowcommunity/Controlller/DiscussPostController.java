package com.nowcoder.nowcommunity.Controlller;

import com.nowcoder.nowcommunity.entity.Comment;
import com.nowcoder.nowcommunity.entity.DiscussPost;
import com.nowcoder.nowcommunity.entity.Page;
import com.nowcoder.nowcommunity.entity.User;
import com.nowcoder.nowcommunity.service.CommentService;
import com.nowcoder.nowcommunity.service.DiscussPostService;
import com.nowcoder.nowcommunity.service.LikeService;
import com.nowcoder.nowcommunity.service.UserService;
import com.nowcoder.nowcommunity.util.CommunityConstant;
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
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostholder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        // 1、检查当前用户是否登录
        User user = hostholder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "你还没有登录哦!");
        }

        if (StringUtils.isBlank(title) || StringUtils.isBlank(content)) {
            return CommunityUtil.getJSONString(405, "帖子标题或帖子内容不能为空!");
        }

        // 2、已登录用户发帖
        DiscussPost discussPost = new DiscussPost();
        discussPost.setTitle(title);
        discussPost.setContent(content);

        discussPost.setCreateTime(new Date());
        discussPost.setStatus(0);
        discussPost.setType(0);
        discussPost.setUserId(user.getId());

        // 3、过滤帖子的内容
        int i = discussPostService.addDiscussPort(discussPost);
        if (i < 1) {
            return CommunityUtil.getJSONString(404, "帖子添加失败!");
        }

        // 报错的情况统一处理
        return CommunityUtil.getJSONString(200, "帖子发布成功!");
    }


    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        // 查询帖子
        DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", discussPost);

        // 查询当前帖子作者
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user", user);

        // 查询帖子的点赞总数
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);
        int likeStatus = 0;

        User loginUser = hostholder.getUser();
        // 查询当前登录用户对帖子的点赞状态
        if (loginUser != null) {
            likeStatus = likeService.findEntityLikeStatus(hostholder.getUser().getId(), ENTITY_TYPE_POST, discussPostId);
        }

        model.addAttribute("likeCount", likeCount);
        model.addAttribute("likeStatus", likeStatus);

        // 评论分页信息
        // 每页显示评论条数：5条
        page.setLimit(5);
        // 评论查询的路径
        page.setPath("/discuss/detail/" + discussPostId);
        // 总评论的数量
        page.setRows(discussPost.getCommentCount());

        // 查询针对帖子的评论：
        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST, discussPost.getId(), page.getOffset(), page.getLimit());

        // 遍历每条帖子的评论，以Map<>来封装每条评论的详细信息
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                Map<String, Object> commentVo = new HashMap<>();
                // 评论的详细信息
                commentVo.put("comment", comment);
                // 评论的作者
                commentVo.put("user", userService.findUserById(comment.getUserId()));

                // 查询帖子的点赞总数
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                likeStatus = 0;
                // 查询当前登录用户对帖子的点赞状态
                if (loginUser != null) {
                    likeStatus = likeService.findEntityLikeStatus(loginUser.getId(), ENTITY_TYPE_COMMENT, comment.getId());
                }

                commentVo.put("likeCount", likeCount);
                commentVo.put("likeStatus", likeStatus);


                // 评论的回复列表
                // 不再分页显示，直接显示全部评论
                List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                List<Map<String, Object>> replyVo = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVoList = new HashMap<>();
                        // 回复信息
                        replyVoList.put("reply", reply);
                        // 评论回复的作者信息
                        replyVoList.put("user", userService.findUserById(reply.getUserId()));
                        // 回复给哪位对话的作者
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVoList.put("target", target);

                        // 查询帖子的点赞总数
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());

                        likeStatus = loginUser == null ? 0 : likeService.findEntityLikeStatus(loginUser.getId(), ENTITY_TYPE_COMMENT, reply.getId());

                        replyVoList.put("likeCount", likeCount);
                        replyVoList.put("likeStatus", likeStatus);

                        // 装入回复结果集
                        replyVo.add(replyVoList);
                    }
                }
                commentVo.put("replys", replyVo);

                // 回复的数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);

                // 将每条评论的详细信息装入集合
                commentVoList.add(commentVo);
            }
        }
        //
        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }
}
