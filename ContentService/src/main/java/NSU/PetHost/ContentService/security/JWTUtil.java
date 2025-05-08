package NSU.PetHost.ContentService.security;

import NSU.PetHost.ContentService.proto.CheckJWT;
import NSU.PetHost.ContentService.proto.JWTServiceGrpc;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JWTUtil {

    public boolean checkToken(String token) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        JWTServiceGrpc.JWTServiceBlockingStub stub = JWTServiceGrpc.newBlockingStub(channel);

        CheckJWT.JWTRequest request = CheckJWT.JWTRequest.newBuilder()
                .setToken(token)
                .build();

        CheckJWT.JWTResponse response = stub.check(request);

        System.out.println(response);
        channel.shutdownNow();

        return response.getCorrectly();
    }

    public String extractClaim(String token, String claim) throws JsonProcessingException {

        // Извлечь payload (вторая часть)
        String[] parts = token.split("\\.");
        String payload = parts[1];

        // Добавим padding, если надо (иначе может быть ошибка при декодировании)
        int paddingLength = (4 - payload.length() % 4) % 4;
        payload += "=".repeat(paddingLength);

        // Декодируем Base64
        byte[] decodedBytes = Base64.getUrlDecoder().decode(payload);
        String decodedPayload = new String(decodedBytes);

        // Парсим JSON
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> claims = mapper.readValue(decodedPayload, Map.class);

        System.out.println("JWT payload claims:");
        claims.forEach((k, v) -> System.out.println(k + ": " + v));

        return claims.get(claim);
    }

}
