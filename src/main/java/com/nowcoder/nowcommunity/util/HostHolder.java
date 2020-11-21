package com.nowcoder.nowcommunity.util;

import com.nowcoder.nowcommunity.entity.User;
import org.springframework.stereotype.Component;

@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    /**
     * 当前服务端响应完成对浏览器的请求后，服务端线程放回线程池，需要实现线程与user信息的解绑
     * 方便当前服务端线程接收其余客户端的请求
     */
    public void remove(){
        users.remove();
    }
}
