package NSU.PetHost.NotificationService.configuration;

import NSU.PetHost.NotificationService.core.model.PersonJWT;
import NSU.PetHost.NotificationService.core.security.JWTUtil;
import NSU.PetHost.NotificationService.core.security.PersonDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);

            if (jwtUtil.checkToken(jwt)) { // Валидация токена (gRPC вызов)
                try {
                    String userIdStr = jwtUtil.extractClaim(jwt, "sub");
                    String username = jwtUtil.extractClaim(jwt, "username");
                    String role = jwtUtil.extractClaim(jwt, "role");

                    if (userIdStr != null && username != null && role != null) {
                        long userId = Long.parseLong(userIdStr);
                        PersonJWT personJwt = new PersonJWT(userId, username, role);
                        UserDetails userDetails = new PersonDetails(personJwt);

                        // Создаем объект Authentication
                        Authentication authentication = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("Authenticated user '{}' with roles {}", username, userDetails.getAuthorities());
                    } else {
                        log.warn("Required claims (sub, username, role) not found in JWT");
                    }
                } catch (NumberFormatException e) {
                    log.warn("Could not parse userId from JWT: {}", e.getMessage());
                } catch (JsonProcessingException e) {
                    log.warn("Could not parse claims from JWT: {}", e.getMessage());
                } catch (Exception e) {
                    log.warn("Error processing JWT: {}", e.getMessage());
                }
            } else {
                log.debug("JWT token validation failed");
            }
        }

        filterChain.doFilter(request, response);
    }
}