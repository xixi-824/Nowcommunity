package com.nowcoder.nowcommunity.Controlller.Interceptor;

import com.nowcoder.nowcommunity.annotation.LoginRequired;
import com.nowcoder.nowcommunity.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        System.out.println("preHandler");
        // 仅针对具体方法进行拦截处理
        if(handler instanceof HandlerMethod){
            // 获取对应的方法
            Method method = ((HandlerMethod) handler).getMethod();
            // 获取方法上的LoginRequired注解
            LoginRequired annotation = method.getAnnotation(LoginRequired.class);
            if(annotation != null && hostHolder.getUser() == null){
                // request.getContextPath()返回项目的根目录
                System.out.println(request.getContextPath());
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }
}
