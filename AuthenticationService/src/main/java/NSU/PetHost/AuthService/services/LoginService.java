package NSU.PetHost.AuthService.services;

import NSU.PetHost.AuthService.dto.requests.AuthenticationDTO;
import NSU.PetHost.AuthService.dto.responses.positive.AuthenticationResponse;
import NSU.PetHost.AuthService.exceptions.Person.PersonNotFoundException;
import NSU.PetHost.AuthService.security.JWTUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public AuthenticationResponse login(@Valid @RequestBody AuthenticationDTO authenticationDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new PersonNotFoundException("Email or password is incorrect");
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authenticationDTO.getNickname(), authenticationDTO.getPassword());

        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            throw new PersonNotFoundException("Email or password is incorrect");
        }

        String accessToken = jwtUtil.generateAccessToken(authenticationToken);
        String refreshToken = jwtUtil.generateRefreshToken(authenticationToken);

        return new AuthenticationResponse(accessToken, refreshToken);
    }

}
