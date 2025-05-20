package NSU.PetHost.ContentService.services;

import NSU.PetHost.ContentService.dto.responses.positive.OkResponse;
import NSU.PetHost.ContentService.exceptions.refusalReasons.RefusalReasonNotFoundException;
import NSU.PetHost.ContentService.models.RefusalReasons;
import NSU.PetHost.ContentService.repositories.RefusalReasonRepository;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class RefusalReasonsService {

    private final RefusalReasonRepository refusalReasonRepository;

    public RefusalReasons getRefusalReasonsByID(long id) {
        return refusalReasonRepository.findById(id).orElseThrow(() -> new RefusalReasonNotFoundException("Refusal reason not found"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    public List<RefusalReasons> getAllReasons() {

        return refusalReasonRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    public OkResponse delete(@Min(1) long id) {

        refusalReasonRepository.deleteById(id);
        return new OkResponse("Refusal reason has deleted", System.currentTimeMillis());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    public OkResponse update(@Min(1) long id, @Size(max = 70) String reason) {

        RefusalReasons refusalReason = getRefusalReasonsByID(id);
        refusalReason.setReason(reason);
        refusalReasonRepository.save(refusalReason);

        return new OkResponse("Refusal reason has updated", System.currentTimeMillis());
    }
}
