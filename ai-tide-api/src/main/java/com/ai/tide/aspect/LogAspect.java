package com.ai.tide.aspect;

import com.ai.tide.entity.OperationLog;
import com.ai.tide.repository.OperationLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

/**
 * Operation Log Aspect
 */
@Aspect
@Component
public class LogAspect {

    @Autowired
    private OperationLogRepository operationLogRepository;

    @Around("@annotation(com.ai.tide.annotation.LogOperation)")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Exception exception = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            try {
                OperationLog log = new OperationLog();
                log.setOperationType(com.ai.tide.enums.OperationType.QUERY);
                log.setModule(joinPoint.getTarget().getClass().getSimpleName());
                log.setOperation(joinPoint.getSignature().getName());
                log.setExecutionTime(executionTime);
                log.setCreatedAt(LocalDateTime.now());

                HttpServletRequest request = getRequest();
                if (request != null) {
                    log.setIpAddress(request.getRemoteAddr());
                    log.setUserAgent(request.getHeader("User-Agent"));
                }

                if (exception != null) {
                    log.setStatus("ERROR");
                    log.setErrorMessage(exception.getMessage());
                } else {
                    log.setStatus("SUCCESS");
                }

                operationLogRepository.save(log);
            } catch (Exception e) {
                // Ignore logging errors
            }
        }
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}
