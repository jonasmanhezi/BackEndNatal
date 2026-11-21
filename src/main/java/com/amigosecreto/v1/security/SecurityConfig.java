package com.amigosecreto.v1.security;

import com.amigosecreto.v1.shared.constant.Commons;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AutenticacaoFilter autenticacaoFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable())
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/me", "/api/me/**").hasRole("PARTICIPANTE")
                .requestMatchers(HttpMethod.GET, "/api/config").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/participants").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/participants").permitAll()
                .requestMatchers(
                    "/docs/**",
                    "/swagger-ui/**",
                    "/v3/api-docs",
                    "/v3/api-docs/**",
                    "/error"
                ).permitAll()
                // Rotas desconhecidas caem no 404 do ApiExceptionHandler
                .anyRequest().permitAll()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> SecurityErrorWriter.escrever(
                        response, 401, "unauthorized",
                        request.getRequestURI().contains("/api/admin") ? Commons.MSG_CHAVE_INVALIDA : Commons.MSG_LINK_INVALIDO))
                .accessDeniedHandler((request, response, accessDeniedException) -> SecurityErrorWriter.escrever(
                        response, 403, "forbidden", "Acesso negado."))
            )
            .addFilterBefore(autenticacaoFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** Sem usuário/senha: evita o Spring gerar uma senha padrão no log. */
    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager();
    }

    /** O filtro roda só dentro do Spring Security, não duas vezes. */
    @Bean
    public FilterRegistrationBean<AutenticacaoFilter> autenticacaoFilterRegistration(AutenticacaoFilter filter) {
        FilterRegistrationBean<AutenticacaoFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
