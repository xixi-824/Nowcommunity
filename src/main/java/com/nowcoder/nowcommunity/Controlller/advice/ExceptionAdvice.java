package com.nowcoder.nowcommunity.Controlller.advice;

import com.nowcoder.nowcommunity.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice
public class ExceptionAdvice {


    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发生异常："+e.getMessage());
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            // 记录每一行的错误信息
            logger.error(stackTraceElement.toString());
        }

        String xRequestedWith = request.getHeader("x-requested-with");
        // 区分异步请求与同步请求
        if("XMLHttpRequest".equals(xRequestedWith)){
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1,"服务器异常!"));
        }else{
            // 同步请求，重定向至500错误页面
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }

}
