package NSU.PetHost.AuthService.config;

import NSU.PetHost.AuthService.security.JWTUtil;
import NSU.PetHost.AuthService.services.PersonDetailsService;
import NSU.PetHost.AuthService.util.exceptions.Person.PersonNotFoundException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final PersonDetailsService personDetailsService;

    public JWTFilter(JWTUtil jwtUtil, PersonDetailsService personDetailsService) {
        this.jwtUtil = jwtUtil;
        this.personDetailsService = personDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String jwt = header.substring(7);

            if (jwt.isBlank()) response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT token in Bearer header");
            else {
                try {
                    String nickname = jwtUtil.verifyTokenAndRetrieveToken(jwt);
                    UserDetails userDetails = personDetailsService.loadUserByUsername(nickname);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch (JWTVerificationException e) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleException(PersonNotFoundException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

}
