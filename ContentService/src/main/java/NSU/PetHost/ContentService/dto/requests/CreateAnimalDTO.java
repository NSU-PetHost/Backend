package NSU.PetHost.ContentService.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;

@Data
public class CreateAnimalDTO {

    @NotBlank(message = "name could not be empty")
    @Size(max = 50)
    @Schema(example = "Misha")
    private String name;

    @Schema(example = "2025-05-18T12:53:09.250Z")
    private Date dateOfBirth;

    @Min(0)
    private Double weight;

    private MultipartFile image;

    private byte[] photo;

    @Min(0)
    @Schema(example = "1")
    private Long petTypeId;


}
