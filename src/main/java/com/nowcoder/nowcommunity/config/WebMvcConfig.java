package com.nowcoder.nowcommunity.config;

import com.nowcoder.nowcommunity.Controlller.Interceptor.FileUploadInterceptor;
import com.nowcoder.nowcommunity.Controlller.Interceptor.LoginRequiredInterceptor;
import com.nowcoder.nowcommunity.Controlller.Interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private FileUploadInterceptor fileUploadInterceptor;

    @Autowired
    private LoginRequiredInterceptor loginRequiredinterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketInterceptor).
                excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg","/kaptcha");
        registry.addInterceptor(fileUploadInterceptor).addPathPatterns("/user/upload**");

        // 注册登录检查的拦截器
        registry.addInterceptor(loginRequiredinterceptor).
                excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");
    }
}
