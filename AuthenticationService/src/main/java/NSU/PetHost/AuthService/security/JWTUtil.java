package NSU.PetHost.AuthService.security;

import NSU.PetHost.AuthService.models.Authority;
import NSU.PetHost.AuthService.models.Person;
import NSU.PetHost.AuthService.repositories.PeopleRepository;
import NSU.PetHost.AuthService.exceptions.Person.PersonNotFoundException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JWTUtil {

    @Value("${jwt_access_secret}")
    private String JWTAccessSecret;

    @Value("${jwt_refresh_secret}")
    private String JWTRefreshSecret;

    private final PeopleRepository peopleRepository;

    private String generateJWT(Person person, Date expirationDate, String JWTToken) {

        return JWT.create()
                .withSubject("Person details")
                .withClaim("nickname", person.getNickname())
                .withClaim("userID", person.getId())
                .withClaim("authorities", person.getAuthorities().stream().map(Authority::getAuthorityName).toList())
                .withIssuer("spring-app")
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(JWTToken));
    }

    public String generateAccessToken(Authentication authentication) {
        Date expirationDate = Date.from(ZonedDateTime.now().plusHours(1).toInstant());

        Person person = peopleRepository.findByNickname(authentication.getName()).orElseThrow(() -> new PersonNotFoundException(Map.of("error", "User with \\\"\" + authentication.getName() + \"\\\" nickname not found")));

        return generateJWT(person, expirationDate, JWTAccessSecret);
    }

    public String generateRefreshToken(Authentication authentication) {
        Date expirationDate = Date.from(ZonedDateTime.now().plusDays(7).toInstant());

        Person person = peopleRepository.findByNickname(authentication.getName()).orElseThrow(() -> new PersonNotFoundException(Map.of("error", "User with \\\"\" + authentication.getName() + \"\\\" nickname not found")));

        return generateJWT(person, expirationDate, JWTRefreshSecret);
    }

    private DecodedJWT verifyJWT(String token, JWTTypes jwtType) {

        String secret = switch (jwtType) {
            case JWTTypes.accessToken -> JWTAccessSecret;
            case JWTTypes.refreshToken -> JWTRefreshSecret;
        };


        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject("Person details")
                .withIssuer("spring-app")
                .build();

        return verifier.verify(token); // тут происходит валидность JWT токена
    }

    public String extractNickname(String token, JWTTypes jwtType) {

        DecodedJWT jwt = verifyJWT(token, jwtType);
        return jwt.getClaim("nickname").asString();
    }

}
