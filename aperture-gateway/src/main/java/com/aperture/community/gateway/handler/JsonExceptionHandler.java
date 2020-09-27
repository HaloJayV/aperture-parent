package com.aperture.community.gateway.handler;

import com.aperture.community.entity.RESULT_BEAN_STATUS_CODE;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.web.reactive.function.server.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: JayV
 * @Date: 2020-9-23 20:07
 * @Description: 自定义JSON异常处理类，代替HTML异常信息
 */
public class JsonExceptionHandler extends DefaultErrorWebExceptionHandler {

    public JsonExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties, ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
    }

    /**
     * 获取异常的属性
     * @param request
     * @param includeStackTrace
     * @return
     */
    @Override
    protected Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("code", RESULT_BEAN_STATUS_CODE.GATEWAY_ERROR);
        map.put("meeage", "网关失败");
        map.put("data", null);
        return map;
    }

    /**
     * 指定响应处理方法为json处理的方法
     * @param errorAttributes
     * @return
     */
    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    /**
     * 获取对应的HttpStatus请求状态
     * @param errorAttributes
     * @return
     */
    @Override
    protected int getHttpStatus(Map<String, Object> errorAttributes) {
        return 200;
    }
}