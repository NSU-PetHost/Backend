package NSU.PetHost.ContentService.DBInit;

import NSU.PetHost.ContentService.models.RefusalReasons;
import NSU.PetHost.ContentService.repositories.RefusalReasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RefusalReasonsInitializer {

    private final RefusalReasonRepository refusalReasonRepository;

    public void initRefusalReasons(){

        if (refusalReasonRepository.count() == 0){

            List<RefusalReasons> refusalReasons = new ArrayList<>();

            refusalReasons.add(new RefusalReasons("Устаревшая или неверная информация"));
            refusalReasons.add(new RefusalReasons("Орфографические ошибки"));
            refusalReasons.add(new RefusalReasons("Жестокость"));
            refusalReasons.add(new RefusalReasons("Нецензурная лексика"));
            refusalReasons.add(new RefusalReasons("Несоответствие тематике сайта"));

            refusalReasonRepository.saveAll(refusalReasons);
        }

    }

}
