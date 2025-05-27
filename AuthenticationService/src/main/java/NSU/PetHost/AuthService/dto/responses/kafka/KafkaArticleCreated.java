package NSU.PetHost.AuthService.dto.responses.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaArticleCreated implements Serializable {

    private long ownerID;

    private long articleID;

    private String status;

}
