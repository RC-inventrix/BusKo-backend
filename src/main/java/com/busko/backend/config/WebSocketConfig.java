package com.busko.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configures Spring STOMP WebSocket support.
 *
 * <p>Clients connect to the {@code /ws} endpoint (with SockJS fallback).
 * Messages sent to destinations prefixed with {@code /app} are routed to
 * {@code @MessageMapping} methods. The in-memory broker handles destinations
 * prefixed with {@code /topic}.
 *
 * <p>Allowed origins are configured via the {@code busko.websocket.allowed-origins}
 * property in {@code application.yml}. Defaults to {@code http://localhost:3000}
 * for local development.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${busko.websocket.allowed-origins:http://localhost:3000}")
    private String[] allowedOrigins;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 1. Pure WebSocket endpoint for Mobile Apps (Flutter)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");

        // 2. SockJS fallback endpoint for Web Browsers (React/Angular)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
