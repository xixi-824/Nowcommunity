package com.nowcoder.nowcommunity.service;

import com.nowcoder.nowcommunity.dao.UserMapper;
import com.nowcoder.nowcommunity.entity.User;
import com.nowcoder.nowcommunity.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class TestService {

    @Autowired
    private UserMapper userMapper;

    public User getUser(){
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        return user;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRES_NEW)
    public Object save1(){
        // 新增用户

        // 插入数据后设置主键
        userMapper.insertUser(getUser());

        return "ok";
    }
}
