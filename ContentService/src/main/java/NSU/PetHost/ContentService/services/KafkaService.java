package NSU.PetHost.ContentService.services;

import NSU.PetHost.ContentService.dto.responses.kafka.KafkaArticleCreated;
import NSU.PetHost.ContentService.dto.responses.kafka.KafkaArticleUpdated;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaTemplate<Long, KafkaArticleCreated> kafkaArticleCreatedTemplate;
    private final KafkaTemplate<Long, KafkaArticleUpdated> kafkaArticleUpdatedTemplate;

    public void addArticleCreated(KafkaArticleCreated kafkaArticleCreated) {
        kafkaArticleCreatedTemplate.send("articleCreated", kafkaArticleCreated);
    }

    public void addArticleUpdated(KafkaArticleUpdated kafkaArticleUpdated) {
        kafkaArticleUpdatedTemplate.send("articleUpdated", kafkaArticleUpdated);
    }

}
