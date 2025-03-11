package com.webank.wedpr.zktransfer.config;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LogAspect {

    @Pointcut("execution(* com.webank.wedpr.zktransfer.controller.*.*(..))")
    public void controllerPointCut() {}

    @Around("controllerPointCut()")
    public Object aroundController(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getName();
        Object[] methodArgs = joinPoint.getArgs();
        log.info("controller request {}({})", methodName, Arrays.toString(methodArgs));
        Object result;
        try {
            result = joinPoint.proceed();
            log.info("controller response methodName:{}, result:{}", methodName, result);
            return result;
        } catch (Throwable throwable) {
            throw throwable;
        }
    }
}
