package com.va.removeconsult.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import web.interceptor.WebSocketInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler(), "/webSocket")
        .addInterceptors(new WebSocketInterceptor())
        .setAllowedOrigins("*");
        registry.addHandler(webSocketHandler(), "/sockjs/webSocket")
        .addInterceptors(new WebSocketInterceptor())
        .setAllowedOrigins("*").withSockJS();
    }

    @Bean
    public MyWebSocketHandler webSocketHandler() {
        return new MyWebSocketHandler();
    }
}
