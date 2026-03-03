package com.deepthinking.core.aspect;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.deepthinking.common.utils.IPUtils;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

/**
 * 控制器切入打印请求日志
 */
@Slf4j
@Aspect
@Component
public class ControllerLogAspect {

    /**
     * 慢方法消耗时间500ms
     */
    private static final long SLOW_METHOD_COST_TIME = 500;
    /**
     * 生产环境标识
     */
    private static final String PROD_ENV_STRING = "ppprd";
    private final String env;

    public ControllerLogAspect(@Value("${spring.profiles.active:dev}") String env) {
        this.env = env;
    }

    String[] nonPrint = new String[]{"/live/active/684631413541241654", "/live/submit"};

    @Around("execution(public * com.optimus.rest..*.*(..))")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();
        if (StringUtils.endsWithAny(request.getRequestURI(), nonPrint[0], nonPrint[1])) {
            return proceedingJoinPoint.proceed();
        }
        StringBuilder logLine = new StringBuilder();
        MethodSignature methodSignature = ((MethodSignature) proceedingJoinPoint.getSignature());
        logLine.append("\n========================================== Start ==========================================");
        logLine.append("\nURL      : ").append(request.getMethod()).append(" ").append(request.getRequestURL()).append(" - ").append(IPUtils.getIpAddr(request));
        logLine.append("\nHeader   : ").append(JSON.toJSONString(getHeaders(request)));
        Map<String, String[]> pm = request.getParameterMap();
        if (CollectionUtil.isNotEmpty(pm)) {
            logLine.append("\nQuery    : ").append(JSON.toJSONString(pm));
        }
        String[] paramNames = methodSignature.getParameterNames();
        Object[] paramValues = proceedingJoinPoint.getArgs();
        Map<Object, Object> param = new HashMap<>(8);
        if (Objects.nonNull(paramNames) && paramNames.length > 0) {
            for (int i = 0; i < paramNames.length; i++) {
                if (paramValues[i] instanceof ServletRequest || paramValues[i] instanceof ServletResponse || paramValues[i] instanceof BindingResult) {
                    continue;
                }
                param.put(paramNames[i], paramValues[i]);
            }
        } else if (null != paramValues && paramValues.length > 0) {
            for (int i = 0; i < paramNames.length; i++) {
                if (paramValues[i] instanceof ServletRequest || paramValues[i] instanceof ServletResponse || paramValues[i] instanceof BindingResult) {
                    continue;
                }
                param.put("var" + i, paramValues[i]);
            }
        }
        logLine.append("\nRequest  : ").append(JSON.toJSONString(param));
        long startTime = System.currentTimeMillis();
        Object result;
        try {
            result = proceedingJoinPoint.proceed();
        } catch (Throwable t) {
            logLine.append("\nException: ").append(t.getMessage());
            logLine.append("\n=========================================== End ===========================================\n");
            log.error(logTimeCost(startTime) + logLine);
            throw t;
        }
        try {
            logLine.append("\nResHeader: ").append(JSON.toJSONString(getHeaders(response)));
            String str = JSON.toJSONString(result);
            if (str.length() > 30000) {
                logLine.append("\nResponse : ").append(str, 0, 3000).append("......");
            } else {
                logLine.append("\nResponse : ").append(str);
            }
            logLine.append("\n=========================================== End ===========================================\n");
            log.info(logTimeCost(startTime) + logLine);
            return result;
        } catch (Throwable t) {
            logLine.append("\nException: ").append(t.getMessage());
            logLine.append("\n=========================================== End ===========================================\n");
            log.error(logTimeCost(startTime) + logLine);
            throw t;
        }
    }

    private String logTimeCost(long startTime){
        long cost = System.currentTimeMillis() - startTime;
        String  timeCost = "TimeCost: " + cost + "ms";
        if (cost >= SLOW_METHOD_COST_TIME) {
            timeCost += " Request_Timeout " + SLOW_METHOD_COST_TIME + " ms";
        }
        return timeCost;
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>(8);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            map.put(key, request.getHeader(key));
        }

        return map;
    }

    private Map<String, String> getHeaders(HttpServletResponse response) {
        Map<String, String> map = new HashMap<>(8);
        Collection<String> headerNames = response.getHeaderNames();
        for (String name : headerNames) {
            map.put(name, response.getHeader(name));
        }
        return map;
    }
}
