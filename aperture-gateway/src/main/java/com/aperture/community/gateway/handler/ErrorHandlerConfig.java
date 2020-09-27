package com.aperture.community.gateway.handler;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.Collections;
import java.util.List;

/**
 * @Auther: JayV
 * @Date: 2020-9-23 20:03
 * @Description: 自定义异常处理类，覆盖默认异常处理
 */
@Configuration
@EnableConfigurationProperties({ServerProperties.class, ResourceProperties.class, ViewResolver.class})
public class ErrorHandlerConfig {

    private final ServerProperties serverProperties;

    private final ApplicationContext applicationContext;

    private final ResourceProperties resourceProperties;

    private final List<ViewResolver> viewResolvers;

    private final ServerCodecConfigurer serverCodecConfigurer;


    public ErrorHandlerConfig(ServerProperties serverProperties,
                              ResourceProperties resourceProperties,
                              ObjectProvider<List<ViewResolver>> viewResolversProvider,
                              ServerCodecConfigurer serverCodecConfigurer,
                              ApplicationContext applicationContext) {
        this.serverProperties = serverProperties;
        this.applicationContext = applicationContext;
        this.resourceProperties = resourceProperties;
        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    /**
     * 异常处理器
     * @param errorAttributes
     * @return
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE) // 优先级最高，覆盖默认处理
    public ErrorWebExceptionHandler errorWebExceptionHandler(ErrorAttributes errorAttributes) {
        JsonExceptionHandler jsonExceptionHandler = new JsonExceptionHandler(errorAttributes,
                this.resourceProperties,
                this.serverProperties.getError(),
                this.applicationContext);

        jsonExceptionHandler.setViewResolvers(this.viewResolvers);
        jsonExceptionHandler.setMessageWriters(this.serverCodecConfigurer.getWriters());
        jsonExceptionHandler.setMessageReaders(this.serverCodecConfigurer.getReaders());
        return jsonExceptionHandler;
    }
}