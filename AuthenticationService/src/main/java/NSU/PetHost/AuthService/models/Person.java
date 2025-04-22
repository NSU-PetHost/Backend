package NSU.PetHost.AuthService.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "persons")
@Getter
@Setter
@NoArgsConstructor
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, name = "id")
    private long id;

    @Column(name = "firstname")
    @NotEmpty(message = "Name should not be empty")
    @Size(max = 100, message = "Name should be between greater 2 and lower 50 characters")
    private String firstName;

    @Column(name = "surname")
    @NotEmpty(message = "Surname should not be empty")
    @Size(max = 100, message = "Surname should be between greater 2 and lower 50 characters")
    private String surname;

    @Column(name = "patronymic")
    @Size(max = 100, message = "Patronymic should be between greater 2 and lower 50 characters")
    private String patronymic;

    @Column(name = "nickname")
    @NotEmpty(message = "Nickname should not be empty")
    @Size(min = 2, max = 100, message = "Surname should be between greater 2 and lower 50 characters")
    private String nickname;

    @Column(name = "password")
    @NotEmpty(message = "Password should not be empty")
    private String password;

    @Column(name = "email")
    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email should not be empty")
    private String email;

    @Column(name = "isemailverified")
    boolean isEmailVerified;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_who")
    private String created_who;

    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(name = "persons_roles",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Role role;

}
