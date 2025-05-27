package NSU.PetHost.ContentService.controllers;

import NSU.PetHost.ContentService.dto.responses.positive.OkResponse;
import NSU.PetHost.ContentService.models.RefusalReasons;
import NSU.PetHost.ContentService.services.RefusalReasonsService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/refusalReasons")
@RequiredArgsConstructor
@Validated
public class RefusalReasonsController {

    private final RefusalReasonsService refusalReasonsService;

    @GetMapping("/getAllReasons")
    @Operation(
            summary = "Admin access. Получение всевозможных причин для отказа"
    )
    public ResponseEntity<List<RefusalReasons>> getAllReasons() {

        return ResponseEntity
                .ok(refusalReasonsService.getAllReasons());
    }

    @GetMapping("/update")
    @Operation(
            summary = "Admin access. Изменение причины для отказа"
    )
    public ResponseEntity<OkResponse> update(@RequestParam @Min(1) long id,
                                             @RequestParam @Size(max = 70) String reason) {

        return ResponseEntity
                .ok(refusalReasonsService.update(id, reason));
    }


    @GetMapping("/delete")
    @Operation(
            summary = "Admin access. Удаление причины для отказа"
    )
    public ResponseEntity<OkResponse> delete(@RequestParam @Min(1) long id) {

        return ResponseEntity
                .ok(refusalReasonsService.delete(id));
    }


}
