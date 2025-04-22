package NSU.PetHost.AuthService.config;

import NSU.PetHost.AuthService.services.PersonDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class SecurityConfig {

    private final JWTFilter jwtFilter;
    private final PersonDetailsService personDetailsService;
    private final JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {

        http
                // Разрешаем доступ к странице логина, разлогирования и регистрации всем
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                .requestMatchers("/api/v1/auth/**").permitAll()
                                .requestMatchers("/api/v1/admin").hasRole("ADMIN") //TODO: заглушка. Поменять потом на права
                                .anyRequest().authenticated()
//                                .anyRequest().permitAll()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .userDetailsService(personDetailsService)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                //отключаем защиту от межсайтовой подделки запросов. При работе с JWT придётся отключить
                .csrf(AbstractHttpConfigurer::disable)
                .passwordManagement(password -> passwordEncoder())
                //Никакая сессия у нас сервере теперь не хранится
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    // Бин для AuthenticationManager (для проверки JWT токена авторизации)
    @Bean
    @Scope("singleton")
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    @Scope("singleton")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
