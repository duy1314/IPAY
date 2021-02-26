package com.duy.ipay.common.interceptor;

import com.duy.ipay.common.utils.SnowflakeIdFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 日志MDC过滤器,追踪请求
 */
@Slf4j
@Component
public class LogInterceptor implements HandlerInterceptor {
    private static final String TRACE_ID = "traceId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //如果想用日志链路追踪，请把traceId放到请求头中
        String traceId = request.getHeader(TRACE_ID);
        if (StringUtils.isEmpty(traceId)) {
            MDC.put(TRACE_ID, SnowflakeIdFactory.nextId());
        } else {
            MDC.put(TRACE_ID, traceId);
        }

        return true;
    }
}
