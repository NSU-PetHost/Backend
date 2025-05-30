package NSU.PetHost.NotificationService.core.security;

import NSU.PetHost.proto.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Component
@Slf4j
// @RequiredArgsConstructor // Может не работать с @Value без дополнительных настроек; лучше явный конструктор или @Autowired на полях
public class JWTUtil {

    // Для gRPC клиента
    private ManagedChannel channel;
    private JWTServiceGrpc.JWTServiceBlockingStub blockingStub;

    @Value("${grpc.client.auth-service.address:pethost-backend-authservice:9090}") // Адрес gRPC сервера авторизации
    private String authServiceAddress;

    @Value("${grpc.client.auth-service.plaintext:true}")
    private boolean usePlaintext;

    @PostConstruct
    private void initGrpcClient() {
        log.info("Initializing gRPC client for AuthService at: {}", authServiceAddress);
        try {
            ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forTarget(authServiceAddress);
            if (usePlaintext) {
                channelBuilder.usePlaintext();
                log.warn("Using plaintext gRPC connection to AuthService. NOT FOR PRODUCTION!");
            } else {
                log.info("Attempting to use transport security for gRPC connection to AuthService.");
            }
            channel = channelBuilder.build();
            blockingStub = JWTServiceGrpc.newBlockingStub(channel);
            log.info("gRPC client for AuthService initialized and channel state: {}", channel.getState(true));
        } catch (Exception e) {
            log.error("Failed to initialize gRPC client for AuthService: {}", e.getMessage(), e);
            // Оставить channel и stub null, методы будут это проверять
        }
    }

    public boolean checkToken(String token) {
        if (blockingStub == null) {
            log.error("gRPC client (blockingStub for AuthService) not initialized. Token check skipped, returning false.");
            return false;
        }

        JWTRequest request = JWTRequest.newBuilder() // Используем сгенерированный JWTRequest
                .setToken(token)
                .build();

        log.debug("Sending JWTRequest to AuthService: token (first 10 chars) = {}", token != null && token.length() > 10 ? token.substring(0, 10) + "..." : token);

        try {
            JWTResponse response = blockingStub.check(request); // Вызываем gRPC метод
            log.debug("Received JWTResponse from AuthService: correctly = {}", response.getCorrectly());
            return response.getCorrectly();
        } catch (StatusRuntimeException e) {
            log.error("gRPC error during token check: {} - {}", e.getStatus(), e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Unexpected error during token check: {}", e.getMessage(), e);
            return false;
        }
    }

    public String extractClaim(String token, String claim) throws JsonProcessingException {
        if (token == null || !token.contains(".")) {
            log.warn("Invalid JWT token format for claim extraction: token is null or does not contain '.'");
            return null;
        }
        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            log.warn("Invalid JWT token format for claim extraction: not enough parts");
            return null;
        }
        String payload = parts[1];

        int paddingLength = (4 - payload.length() % 4) % 4;
        payload += "=".repeat(paddingLength);

        try {
            byte[] decodedBytes = Base64.getUrlDecoder().decode(payload);
            String decodedPayload = new String(decodedBytes);

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> claims = mapper.readValue(decodedPayload, new TypeReference<>() {});

            Object raw = claims.get(claim);
            return raw != null ? raw.toString() : null;
        } catch (IllegalArgumentException e) {
            log.error("Error decoding Base64 payload from JWT: {}", e.getMessage());
            return null; // или throw JsonProcessingException
        }
    }

    @PreDestroy
    public void shutdownGrpcClient() {
        if (channel != null && !channel.isShutdown()) {
            log.info("Shutting down gRPC client channel to AuthService...");
            try {
                channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                log.info("gRPC client channel to AuthService shutdown complete.");
            } catch (InterruptedException e) {
                log.warn("gRPC client channel shutdown to AuthService interrupted.", e);
                Thread.currentThread().interrupt();
            }
        }
    }
}