package NSU.PetHost.AuthService.controllers;

import NSU.PetHost.AuthService.dto.RegistrationDTO;
import NSU.PetHost.AuthService.models.Person;
import NSU.PetHost.AuthService.security.JWTUtil;
import NSU.PetHost.AuthService.services.PersonService;
import NSU.PetHost.AuthService.services.RegistrationService;
import NSU.PetHost.AuthService.util.exceptions.Person.PersonNotCreatedException;
import NSU.PetHost.AuthService.util.exceptions.Person.PersonWithThisEmailExistsException;
import NSU.PetHost.AuthService.util.exceptions.Person.PersonWithThisNicknameExistsException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/")
public class RegistrationController {

    private final ModelMapper modelMapper;
    private final RegistrationService registrationService;
    private final PersonService personService;
    private final JWTUtil jwtUtil;

    @Autowired
    public RegistrationController(ModelMapper modelMapper, RegistrationService registrationService, PersonService personService, JWTUtil jwtUtil) {
        this.modelMapper = modelMapper;
        this.registrationService = registrationService;
        this.personService = personService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/registration")
    public ResponseEntity<Map<String, Object>> registration(@RequestBody @Valid RegistrationDTO registrationDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, Object> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()));

            throw new PersonNotCreatedException(errors);
        }

        Person person = convertToPerson(registrationDTO);

        personService.checkExistingPerson(person);

        registrationService.registerPerson(person);

        Map<String, Object> response = new HashMap<>();
        response.put("jwt", jwtUtil.generateToken(person.getNickname()));
        response.put("id", person.getId()); // это хранится в JWT. Есть ли смысл возвращать отдельными полями?
        response.put("nickname", person.getNickname());
        response.put("authorities", person.getAuthorities());

        return ResponseEntity.accepted().body(response);
    }

    private Person convertToPerson(@Valid RegistrationDTO registrationDTO) {
        return modelMapper.map(registrationDTO, Person.class);
    }

    private RegistrationDTO convertToRegistrationDTO(Person person) {
        return modelMapper.map(person, RegistrationDTO.class);
    }

    @ExceptionHandler(PersonNotCreatedException.class)
    private ResponseEntity<Map<String, Object>> handleException(PersonNotCreatedException ex) {
        return ResponseEntity
                .badRequest()
                .body(Map.of("errors", ex.getErrors()));
    }

    @ExceptionHandler(PersonWithThisEmailExistsException.class)
    private ResponseEntity<Map<String, Object>> handleException(PersonWithThisEmailExistsException ex) {
        return ResponseEntity
                .badRequest()
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(PersonWithThisNicknameExistsException.class)
    private ResponseEntity<Map<String, Object>> handleException(PersonWithThisNicknameExistsException ex) {
        return ResponseEntity
                .badRequest()
                .body(Map.of("errors", ex.getMessage()));
    }

}
