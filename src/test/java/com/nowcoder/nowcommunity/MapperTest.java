package com.nowcoder.nowcommunity;

import com.nowcoder.nowcommunity.dao.DiscussPostMapper;
import com.nowcoder.nowcommunity.dao.LoginTicketMapper;
import com.nowcoder.nowcommunity.dao.UserMapper;
import com.nowcoder.nowcommunity.entity.LoginTicket;
import com.nowcoder.nowcommunity.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

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

    @Autowired
    private LoginTicketMapper loginTicketMapper;

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
    public void testInsertLoginTicket(){
        LoginTicket ticket = new LoginTicket();
        ticket.setStatus(0);
        ticket.setTicket("def");
        ticket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 *10));

        loginTicketMapper.insertLoginTicket(ticket);
    }

    @Test
//    @Transactional(rollbackFor = Throwable.class)
    public void SelectAndUpdateLoginTicket(){
        // 第一次查询，缓存至sqlsession中
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("def");
        System.out.println(loginTicket);
//        int abc = loginTicketMapper.updateStatus("def", 1);
        // 第二次查询，直接读取一级缓存
        loginTicket = loginTicketMapper.selectByTicket("def");
        System.out.println(loginTicket);
    }
}
