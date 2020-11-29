package com.nowcoder.nowcommunity.Controlller;

import com.nowcoder.nowcommunity.entity.DiscussPost;
import com.nowcoder.nowcommunity.entity.Page;
import com.nowcoder.nowcommunity.entity.User;
import com.nowcoder.nowcommunity.service.DiscussPostService;
import com.nowcoder.nowcommunity.service.LikeService;
import com.nowcoder.nowcommunity.service.UserService;
import com.nowcoder.nowcommunity.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lei
 * @date 2020/8/2 10:19
 */
@Controller
public class HomeController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    // 用于显示前端需要的数据，例如将userId转换为userName
    // 增加界面的人机友好性
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/index",method = RequestMethod.GET)
    // 返回是一个HTML页面，不用加@ResponseBody注解
    public String getIndexPage(Model model, Page page){
        // 方法调用前，SpringMVC会自动实例化Model和Page，并将Page注入Model
        // 所以，在thymeleaf中可以直接访问Page对象中的数据
        // 查询对应userId的数据行数
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        // 封装数据至集合中
        List<DiscussPost> list = discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit());
        // userId转换为username
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null){
            for (DiscussPost post : list){
                Map<String,Object> map = new HashMap<>();
                map.put("post",post);
                User user = userService.findUserById(post.getUserId());
                map.put("user",user);

                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount",likeCount);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        return "/index";
    }

    @RequestMapping(path="/error",method=RequestMethod.GET)
    public String getErrorPage(){
        return "/error/500";
    }
}
