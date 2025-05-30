package NSU.PetHost.NotificationService.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Collections;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Autowired
    private JwtDecoder jwtDecoder;
     //Регистрирует STOMP эндпоинты, к которым будут подключаться клиенты.
     //"/ws" - это путь, по которому клиенты будут устанавливать WebSocket соединение.
     //withSockJS() - для поддержки SockJS как fallback, если WebSocket не поддерживается браузером (опционально).
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-notifications")
                .setAllowedOriginPatterns("*") // Разрешить подключения с любых доменов (для разработки)
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Префикс для сообщений, которые отправляются ОТ КЛИЕНТА К СЕРВЕРУ
        registry.setApplicationDestinationPrefixes("/app");

        // Префикс для сообщений, которые отправляются ОТ СЕРВЕРА К КЛИЕНТАМ (подписка на топики)
        // Клиенты будут подписываться на топики, начинающиеся с "/topic" или "/user" (для персональных сообщений)
        registry.enableSimpleBroker("/topic", "/user"); 
        
        // Для персональных сообщений (отправка конкретному пользователю) используется префикс /user
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(org.springframework.messaging.simp.config.ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // Пытаемся извлечь токен из заголовка
                    List<String> authorizationHeaders = accessor.getNativeHeader("Authorization");
                    if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
                        String authHeader = authorizationHeaders.get(0);
                        if (authHeader != null && authHeader.startsWith("Bearer ")) {
                            String token = authHeader.substring(7);
                            try {
                                Jwt jwt = jwtDecoder.decode(token);
                                UsernamePasswordAuthenticationToken userAuth =
                                        new UsernamePasswordAuthenticationToken(jwt.getSubject(), null, Collections.emptyList());
                                accessor.setUser(userAuth);
                                log.info("WebSocket user authenticated: {}", jwt.getSubject());
                            } catch (Exception e) {
                                log.warn("WebSocket authentication failed for token: {}", e.getMessage());
                            }
                        }
                    }
                }
                return message;
            }
        });
    }
}