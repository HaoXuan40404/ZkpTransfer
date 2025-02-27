package com.webank.ppc.iss.monitor;

import static java.time.LocalDateTime.now;

import java.time.LocalDateTime;
import java.util.Objects;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

@EnableAspectJAutoProxy
@Component
@Aspect
public class DbOpAspect {
    /** 注入ims日志 */
    @Pointcut("execution(* com.webank.ppc.iss.controller.*.*(..))")
    private void pointCutMethod() {}

    @Around("pointCutMethod()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getName();
        Object[] methodArgs = joinPoint.getArgs();

        LocalDateTime start = now();
        Object result;
        try {
            result = joinPoint.proceed();
            String bizSeq = "";
            String sysSeq = "";

            if (Objects.nonNull(methodArgs) && methodArgs.length > 1) {
                bizSeq = String.valueOf(methodArgs[0]);
                sysSeq = String.valueOf(methodArgs[1]);
            }

            MonitorLogUtils.printMonitorLog(
                    bizSeq, sysSeq, methodName, start, MonitorErrorType.SUCCESS);
            return result;
        } catch (Throwable throwable) {
            MonitorLogUtils.printMonitorLog(methodName, start, MonitorErrorType.BIZ_ERROR);
            throw throwable;
        }
    }
}
