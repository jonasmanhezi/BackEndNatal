package com.amigosecreto.v1.security;

import com.amigosecreto.v1.core.port.in.ParticipantePortIn;
import com.amigosecreto.v1.shared.constant.Commons;
import com.amigosecreto.v1.shared.ratelimit.LimitadorTentativas;
import com.amigosecreto.v1.shared.token.TokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Sem contas: o participante se identifica pelo token pessoal ({@code Authorization: Bearer})
 * e o organizador pela chave ({@code X-Admin-Key}). Só um dos dois vale por rota.
 */
@Component
@Slf4j
public class AutenticacaoFilter extends OncePerRequestFilter {

    public static final String ADMIN_HEADER = "X-Admin-Key";
    public static final String ATRIBUTO_PARTICIPANTE_ID = "participanteId";
    public static final String ROLE_PARTICIPANTE = "ROLE_PARTICIPANTE";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final ParticipantePortIn participanteService;
    private final String adminKey;
    private final LimitadorTentativas falhasAdmin;
    private final LimitadorTentativas cadastros;

    public AutenticacaoFilter(
            ParticipantePortIn participanteService,
            @Value("${app.admin-key:}") String adminKey,
            @Value("${app.rate-limit.falhas-admin-por-15min:10}") int falhasAdminPor15Min,
            @Value("${app.rate-limit.cadastros-por-hora:30}") int cadastrosPorHora
    ) {
        this.participanteService = participanteService;
        this.adminKey = adminKey == null ? "" : adminKey.trim();
        this.falhasAdmin = new LimitadorTentativas(falhasAdminPor15Min, Duration.ofMinutes(15));
        this.cadastros = new LimitadorTentativas(cadastrosPorHora, Duration.ofHours(1));

        if (this.adminKey.isEmpty()) {
            log.warn("ADMIN_KEY não definida: as rotas /api/admin/* ficam fechadas.");
        } else if (this.adminKey.length() < 16) {
            log.warn("ADMIN_KEY curta demais. Use pelo menos 16 caracteres.");
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String caminho = request.getRequestURI().substring(request.getContextPath().length());
        String ip = request.getRemoteAddr();

        if (caminho.startsWith("/api/admin")) {
            if (falhasAdmin.bloqueado(ip)) {
                SecurityErrorWriter.escrever(response, 429, "too_many", Commons.MSG_MUITAS_TENTATIVAS);
                return;
            }
            if (chaveAdminValida(request.getHeader(ADMIN_HEADER))) {
                autenticar("admin", ROLE_ADMIN);
            } else if (!"OPTIONS".equals(request.getMethod())) {
                falhasAdmin.registrar(ip);
            }
        } else if (caminho.startsWith("/api/me")) {
            autenticarParticipante(request);
        } else if (caminho.equals("/api/participants") && "POST".equals(request.getMethod())) {
            if (cadastros.bloqueado(ip)) {
                SecurityErrorWriter.escrever(response, 429, "too_many", Commons.MSG_MUITAS_TENTATIVAS);
                return;
            }
            cadastros.registrar(ip);
        }

        filterChain.doFilter(request, response);
    }

    private void autenticarParticipante(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return;
        }

        Optional<Long> participanteId = participanteService.buscarIdPorToken(header.substring(7));
        participanteId.ifPresent(id -> {
            autenticar(id, ROLE_PARTICIPANTE);
            request.setAttribute(ATRIBUTO_PARTICIPANTE_ID, id);
        });
    }

    private boolean chaveAdminValida(String recebida) {
        return !adminKey.isEmpty() && recebida != null && TokenUtil.iguais(adminKey, recebida.trim());
    }

    private void autenticar(Object principal, String role) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, List.of(new SimpleGrantedAuthority(role)));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
