package NSU.PetHost.AuthService.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence (HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

//        if (authException instanceof UsernameNotFoundException) { //TODO: разобраться как обрабатывать именно это исключение
            // Устанавливаем код ошибки 401 и формируем JSON-ответ с сообщением
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(String.format("{\"error\": \"%s \"}", authException.getMessage()));
//        }

    }
}
