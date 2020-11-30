package com.nowcoder.nowcommunity.Controlller.Interceptor;

import com.nowcoder.nowcommunity.entity.LoginTicket;
import com.nowcoder.nowcommunity.entity.User;
import com.nowcoder.nowcommunity.service.UserService;
import com.nowcoder.nowcommunity.util.CookieUtil;
import com.nowcoder.nowcommunity.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("preHandler");
        // 1、获取登录凭证
        String ticket = CookieUtil.getCookie(request, "ticket");
        // 2、检查登录凭证的有效性
        if(ticket != null){
            LoginTicket loginTicket = userService.getLoginTicket(ticket);
            // 检查凭证的有效性
            if(loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                // 凭证有效，查询用户信息
                User user = userService.findUserById(loginTicket.getUserId());
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    /**
     * handler处理器执行之后，将线程绑定的user信息装入model中
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("postHandle");
        User user = hostHolder.getUser();
        if(user != null && modelAndView != null){
            modelAndView.addObject("loginUser",user);
        }
    }

    /**
     * 执行完视图解析器进行前端界面演示时，本次客户端请求，服务端已经完成响应，对应线程会收回线程池
     * 需要实现user与当前线程的解绑，当前线程不会销毁，而是继续用于执行其余用户的请求
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("afterCompletion");
        hostHolder.remove();
    }
}
