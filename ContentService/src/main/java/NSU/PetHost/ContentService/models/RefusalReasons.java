package NSU.PetHost.ContentService.models;

import jakarta.persistence.*;

@Entity
@Table(name = "refusal_reasons")
public class RefusalReasons {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(length = 70)
    private String reason;

}


//                  Устаревшая или неверная информация
//                  Орфографические ошибки
//                  Жестокость
//                  Нецензурная лексика
//                  Несоответствие тематике сайта