package NSU.PetHost.AuthService.services;

import NSU.PetHost.AuthService.dto.requests.RefreshDTO;
import NSU.PetHost.AuthService.dto.responses.positive.AuthenticationResponse;
import NSU.PetHost.AuthService.exceptions.Person.RefreshTokenNotFound;
import NSU.PetHost.AuthService.security.JWTTypes;
import NSU.PetHost.AuthService.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
@RequiredArgsConstructor
public class RefreshService {

    private final JWTUtil jwtUtil;

    public AuthenticationResponse refresh(RefreshDTO refreshDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new RefreshTokenNotFound("Refresh token is incorrect");
        }

        String nickname = jwtUtil.extractClaim(refreshDTO.getRefreshToken(), JWTTypes.refreshToken, "nickname");

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(nickname, null, null);

        String accessToken = jwtUtil.generateAccessToken(authenticationToken);
        String refreshToken = jwtUtil.generateRefreshToken(authenticationToken);

        return new AuthenticationResponse(accessToken, refreshToken);
    }

}
