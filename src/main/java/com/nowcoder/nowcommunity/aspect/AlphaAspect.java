package com.nowcoder.nowcommunity.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

//@Component
//@Aspect
public class AlphaAspect {

    @Pointcut("execution(* com.nowcoder.nowcommunity.service.*.*(..))")
    public void pointcut() {

    }

    /**
     * 前置通知，方法执行前执行
     */
    @Before("pointcut()")
    public void before() {
        System.out.println("before");
    }

    @After(value = "pointcut()")
    public void after() {
        System.out.println("after");
    }

    @AfterReturning(value = "pointcut()")
    public void afterReturning() {
        System.out.println("afterReturning");
    }

    @AfterThrowing(value ="pointcut()")
    public void afterthrowing(){
        System.out.println("afterThrowing");
    }

    @Around(value = "pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("around before");
        Object obj = joinPoint.proceed();
        System.out.println("around after");
        return obj;
    }
}
