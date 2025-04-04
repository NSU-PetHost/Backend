package NSU.PetHost.AuthService.config;

import NSU.PetHost.AuthService.services.PersonDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
public class SecurityConfig {

    private final JWTFilter jwtFilter;
    private final PersonDetailsService personDetailsService;
    private final JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    public SecurityConfig(JWTFilter jwtFilter, PersonDetailsService personDetailsService, JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtFilter = jwtFilter;
        this.personDetailsService = personDetailsService;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {

        http
                // Разрешаем доступ к странице логина, разлогирования и регистрации всем
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/admin").hasRole("ADMIN")
                        .requestMatchers("/api/v1/main_page", "/api/v1/auth/login", "/api/v1/auth/logout", "/api/v1/auth/registration").permitAll()
                        .anyRequest().authenticated()
                )
                // Настраиваем форму логина
                .formLogin(form -> form
                        .loginPage("/api/v1/auth/login")
                        .loginProcessingUrl("/api/v1/process_login")
                        .defaultSuccessUrl("/api/v1/main_page", true)
                        .failureUrl("/api/v1/auth/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/main_page")
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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
