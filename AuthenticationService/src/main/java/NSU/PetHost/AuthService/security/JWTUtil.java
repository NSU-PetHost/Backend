package NSU.PetHost.AuthService.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class JWTUtil {

    @Value("${jwt_secret}")
    private String secret;

    public String generateToken(String nickname) {
        Date expirationDate = Date.from(ZonedDateTime.now().plusHours(1).toInstant());

        return JWT.create()
                .withSubject("User details")
                .withClaim("nickname", nickname)
                .withIssuer("spring-app")
                .withIssuedAt(new Date())
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secret));
    }

    public String verifyTokenAndRetrieveToken(String token) {

        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject("User details")
                .withIssuer("spring-app")
                .build();

        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("nickname").asString();
    }

}
