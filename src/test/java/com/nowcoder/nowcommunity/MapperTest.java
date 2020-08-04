package com.nowcoder.nowcommunity;

import com.nowcoder.nowcommunity.dao.DiscussPostMapper;
import com.nowcoder.nowcommunity.dao.UserMapper;
import com.nowcoder.nowcommunity.entity.DiscussPost;
import com.nowcoder.nowcommunity.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

/**
 * @author lei
 * @date 2020/7/26 13:18
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NowcommunityApplication.class)
public class MapperTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectByUser(){
        User user = userMapper.selectById(101);
        System.out.println(user);
//
//        user = userMapper.selectByName("liubei");
//        System.out.println(user);
//
//        user = userMapper.selectByEmail("nowcoder101@sina.com");
//        System.out.println(user);
    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101/png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void updateUser(){
        int rows = userMapper.updateStatus(150,1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150,"http://www.nowcoder.com/102/png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150,"1234");
        System.out.println(rows);
    }

    @Test
    public void testASelectPosts(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(102,0,10);
        for (DiscussPost discussPost:list){
            System.out.println(discussPost);
        }

        int rows = discussPostMapper.selectDiscussPostRows(103);
        System.out.println(rows);
    }
}
