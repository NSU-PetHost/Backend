package NSU.PetHost.NotificationService.core.service.client;

import NSU.PetHost.proto.*;
import NSU.PetHost.proto.PersonResponse;
import NSU.PetHost.proto.PersonServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class PersonServiceClient {
    private ManagedChannel channel;
    private PersonServiceGrpc.PersonServiceBlockingStub blockingStub;

    @Value("${grpc.client.auth-service.address}")
    private String serviceAddress;

    @PostConstruct
    private void init() {
        try {
            channel = ManagedChannelBuilder.forTarget(serviceAddress)
                    .usePlaintext()
                    .build();
            blockingStub = PersonServiceGrpc.newBlockingStub(channel);
            log.info("gRPC PersonServiceClient initialized for address: {}", serviceAddress);
        } catch (Exception e) {
            log.error("Failed to initialize gRPC PersonServiceClient: {}", e.getMessage(), e);
        }
    }

    public Optional<PersonResponse> getPersonById(long personId) {
        if (blockingStub == null) {
            log.warn("gRPC stub is not initialized. Cannot fetch person.");
            return Optional.empty(); // заглушка, если клиент не инициализирован
        }
        try {
            PersonRequest request = PersonRequest.newBuilder().setPersonId(personId).build();
            PersonResponse response = blockingStub.getPersonById(request);
            return Optional.of(response);
        } catch (Exception e) {
            log.error("Error calling gRPC GetPersonById for personId {}: {}", personId, e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<PersonResponse> getPersonByEmail(String email) {
        if (blockingStub == null) { /* ... лог и возврат empty ... */ return Optional.empty(); }
        PersonEmailRequest request = PersonEmailRequest.newBuilder().setEmail(email).build();
        try {
            log.debug("Requesting user data by email: {}", email);
            PersonResponse response = blockingStub.getUserByEmail(request);
            return (response == null || response.getId() == 0) ? Optional.empty() : Optional.of(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC error fetching user by email {}: {}", email, e.getStatus());
            return Optional.empty();
        }
    }

    public List<Long> getAllPersonIds() {
        if (blockingStub == null) {
            log.warn("gRPC stub is not initialized. Cannot fetch all person IDs.");
            return Collections.emptyList() ;
            // если сервис недоступен, вернем тестового пользователя
            // а вообще надо Collections.emptyList()
        }
        try {
            Empty request = Empty.newBuilder().build();
            PersonIdsResponse response = blockingStub.getAllPersonIds(request);
            return response.getPersonIdsList();
        } catch (Exception e) {
            log.error("Error calling gRPC GetAllPersonIds: {}", e.getMessage());
            // если сервис недоступен, вернем тестового пользователя
            // а вообще надо Collections.emptyList()
            return Collections.emptyList() ;
        }
    }


    @PreDestroy
    public void shutdown() throws InterruptedException {
        if (channel != null) {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            log.info("gRPC PersonServiceClient shutdown.");
        }
    }
}