package com.aperture.community.gateway.filter;

import com.google.gson.JsonObject;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.OrderUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Auther: JayV
 * @Date: 2020-9-23 19:40
 * @Description: 全局认证过滤器（拦截器），统一处理外部拒绝访问和会员登录的服务
 */
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    // 路径匹配器
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 执行拦截器
     * @param exchange 服务网络交换器
     * @param chain 拦截器链
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        // 获取请求的url地址
        String path = request.getURI().getPath();

        // 设置拦截的api接口路径匹配，请求该资源的用户必须登录
        if(antPathMatcher.match("/api/**/auth/**", path)) {
            // 获取请求头的token值
            List<String> tokenList = request.getHeaders().get("token");
            if(tokenList == null) {
                //
                ServerHttpResponse response = exchange.getResponse();
                // 没有token表示还未登录，返回认证失败的响应体
                return out(response);
            } else {
                ServerHttpResponse response = exchange.getResponse();
                return out(response);
            }
        }
        // 内部接口，不允许外界访问
        if(antPathMatcher.match("/**/inner/**", path)) {
            ServerHttpResponse response = exchange.getResponse();
            return out(response);
        }
        return chain.filter(exchange);
    }

    /**
     * 设置该拦截器的拦截顺序
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * 认证失败并返回响应体
     * @param response
     * @return
     */
    private Mono<Void> out(ServerHttpResponse response) {
        JsonObject message = new JsonObject();
        message.addProperty("success", false);
        message.addProperty("code", 28004);
        message.addProperty("data", "认证失败");
        byte[] bytes = message.toString().getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        // 设置响应体的编码
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }
}