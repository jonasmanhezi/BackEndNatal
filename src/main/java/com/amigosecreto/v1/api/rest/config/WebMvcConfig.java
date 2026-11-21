package com.amigosecreto.v1.api.rest.config;

import com.amigosecreto.v1.security.AuthenticatedParticipanteIdArgumentResolver;
import com.amigosecreto.v1.security.AuthenticatedParticipanteTokenArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthenticatedParticipanteIdArgumentResolver authenticatedParticipanteIdArgumentResolver;
    private final AuthenticatedParticipanteTokenArgumentResolver authenticatedParticipanteTokenArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticatedParticipanteIdArgumentResolver);
        resolvers.add(authenticatedParticipanteTokenArgumentResolver);
    }
}
