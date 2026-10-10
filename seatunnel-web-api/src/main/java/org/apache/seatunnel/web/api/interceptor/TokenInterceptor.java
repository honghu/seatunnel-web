package org.apache.seatunnel.web.api.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.seatunnel.web.spi.bean.entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    private static final String SESSION_KEY_PREFIX = "spring:session:sessions:";
    private static final String HEADER_TOKEN = "token";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${profiles.active}")
    private String profilesActive;

    // 构造注入（SpringBoot3推荐，代替@Resource）
    public TokenInterceptor(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 放行OPTIONS跨域预检请求，前端跨域必加！
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        if (profilesActive.equals("dev")){
            return true;
        }

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();

        // 1. 获取header token
        String token = request.getHeader(HEADER_TOKEN);
        if (token == null || token.isBlank()) {
            writer.write(objectMapper.writeValueAsString(Result.buildFailure("没有登录")));
            writer.flush();
            return false;
        }

        // 2. 查询redis spring session key
        String sessionRedisKey = SESSION_KEY_PREFIX + token;
        Boolean exist = redisTemplate.hasKey(sessionRedisKey);
        if (!Boolean.TRUE.equals(exist)) {
            writer.write(objectMapper.writeValueAsString(Result.buildFailure("登录超时，重新登录")));
            writer.flush();
            return false;
        }

        // token有效，放行
        return true;
    }
}
