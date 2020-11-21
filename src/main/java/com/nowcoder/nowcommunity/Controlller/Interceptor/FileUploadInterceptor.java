package com.nowcoder.nowcommunity.Controlller.Interceptor;

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class FileUploadInterceptor extends HandlerInterceptorAdapter {

    // 文件最大容量()
    private long maxSize = 1048576L;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws FileSizeLimitExceededException {
        // 判断文件是否上传
        if(request != null && ServletFileUpload.isMultipartContent(request)){
            ServletRequestContext ctx =  new ServletRequestContext(request);
            // 获取上传文件尺寸大小
            long requestSize = ctx.contentLength();
            if(requestSize > maxSize){
                // 上传文件大小超过指定限制后，
                // 模拟抛出MaxUploadSizeExceededException异常
                request.setAttribute("error","文件大小超过1MB");
            }
        }
        return true;
    }
}
