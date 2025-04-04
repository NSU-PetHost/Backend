package NSU.PetHost.AuthService.controllers;

import NSU.PetHost.AuthService.dto.AuthenticationDTO;
import NSU.PetHost.AuthService.repositories.AuthorityRepository;
import NSU.PetHost.AuthService.security.JWTUtil;
import NSU.PetHost.AuthService.security.PersonDetails;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/login")
    public ResponseEntity<Map<String, Object>> login() { //TODO: вопрос с проверкой JWT и нужно ли здесь DTO. Что возвращать?

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Object principal = authentication.getPrincipal();

        if (principal instanceof PersonDetails) {
            PersonDetails personDetails = (PersonDetails) principal;
            return ResponseEntity.ok(Map.of("personDetails", personDetails));
        } else if (principal instanceof String) {
            // Если principal – это просто String (например, username), можно вернуть это значение.
            String username = (String) principal;
            return ResponseEntity.ok(Map.of("username", username));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Unexpected principal type"));
        }
    }

    //Проверка валидности данных и выдача нового JWT токена
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody AuthenticationDTO authenticationDTO) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authenticationDTO.getNickname(), authenticationDTO.getPassword());

        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid nickname or password"));
        }

        String token = jwtUtil.generateToken(authenticationDTO.getNickname());
        return ResponseEntity.ok().body(Map.of("token", token));
    }



}
