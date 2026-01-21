package com.example.login. security;

import com.example.login.entity.Usuario;
import com.example.login.service.UsuarioService;
import org.springframework.context. annotation.Bean;
import org. springframework.context.annotation.Configuration;
import org.springframework.security. config.annotation.web.builders. HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security. crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security. web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet. http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class WebSecurityConfig {

    private final UsuarioService usuarioService;

    public WebSecurityConfig(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/logout", "/css/**", "/js/**", "/error").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasRole("USUARIO")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(customAuthenticationSuccessHandler())
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/spring-logout")  // Cambiar la URL default para evitar conflictos
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            String username = authentication.getName();
            try {
                Usuario usuario = usuarioService.buscarPorUsername(username);
                usuario.actualizarUltimoLogin();
                usuarioService. crearUsuario(usuario);
            } catch (Exception e) {
                System.err.println("Error actualizando ultimo_login: " + e.getMessage());
            }

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (isAdmin) {
                response.sendRedirect("/admin/usuarios");
            } else {
                response.sendRedirect("/user/home");
            }
        };
    }
}