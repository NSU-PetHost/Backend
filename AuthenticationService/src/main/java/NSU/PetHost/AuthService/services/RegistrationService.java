package NSU.PetHost.AuthService.services;

import NSU.PetHost.AuthService.dto.requests.RegistrationDTO;
import NSU.PetHost.AuthService.dto.requests.VerifyAccountDTO;
import NSU.PetHost.AuthService.dto.responses.positive.RegistrationResponse;
import NSU.PetHost.AuthService.exceptions.Person.ConfirmEmailException;
import NSU.PetHost.AuthService.exceptions.Person.PersonNotCreatedException;
import NSU.PetHost.AuthService.exceptions.Person.PersonNotFoundException;
import NSU.PetHost.AuthService.models.Person;
import NSU.PetHost.AuthService.models.VerifyCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final PersonService personService;
    private final MailSenderService mailSenderService;
    private final KafkaService kafkaService;
    private final RedisService redisService;

    public RegistrationResponse registration(@RequestBody @Valid RegistrationDTO registrationDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, Object> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()));

            throw new PersonNotCreatedException(errors);
        }

        Person person = personService.convertToPerson(registrationDTO);

        personService.registerPerson(person);

        return new RegistrationResponse("Registration complete");
    }

    public RegistrationResponse confirmEmail(@Valid @RequestBody String email, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, Object> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()));

            throw new ConfirmEmailException(errors);
        }

        if (!personService.isExistingPersonFromEmail(email)) {
            throw new PersonNotFoundException("Person with " + email + " not exists");
        }

        VerifyCode verifyCode = new VerifyCode(email, generateVerifyCode(), false);

        redisService.addVerifyCode(verifyCode);

        kafkaService.addVerifyCodeInConfirmMailTopic(verifyCode);

        return new RegistrationResponse("Email sent");
    }

    public RegistrationResponse confirmCode(@RequestBody @Valid VerifyAccountDTO verifyAccountDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, Object> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()));

            throw new ConfirmEmailException(errors);
        }

        VerifyCode verifyCode = redisService.findVerifyCode(verifyAccountDTO.getEmail());

        if (verifyCode == null) {
             throw new ConfirmEmailException(Map.of("error", "Verification code expired"));
        }

        if (verifyCode.getCode() == verifyAccountDTO.getVerifyCode()) {
            personService.setEmailVerified(verifyAccountDTO.getEmail());
        } else {
            throw new ConfirmEmailException(Map.of("error", "Verification code invalid"));
        }

        verifyCode.setVerified(true);
        redisService.addVerifyCode(verifyCode);

        return new RegistrationResponse("Code verified");

    }

    public static int generateVerifyCode() {
        return (int)(Math.random() * 900000) + 100000; // in range [100000;999999]
    }

}
