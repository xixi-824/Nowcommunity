package com.nowcoder.nowcommunity.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Aspect
public class ServiceLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    // 配置切面
    // spring aop 是方法级别的aop
    @Pointcut("execution(* com.nowcoder.nowcommunity.service.*.*(..))")
    public void pointcut(){

    }

    @Before(value = "pointcut()")
    public void before(JoinPoint joinPoint){
        // 用户[1.2.3.4],在[xxx]访问了[com.nowcoder.community.service.xxx]
        // 1、获取request对象
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        // 2、获取客户端的ip
        String ip = request.getRemoteHost();
        // 3、获取记录日志的时间
        String now = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
        // 4、获取访问的全限定类名及方法名
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        logger.info(String.format("用户[%s],在[%s],访问了[%s].",ip,now,target));
    }
}
