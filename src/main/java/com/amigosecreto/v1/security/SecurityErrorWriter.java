package com.amigosecreto.v1.security;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** Erros dos filtros no mesmo formato JSON do ApiExceptionHandler: {"code", "message"}. */
final class SecurityErrorWriter {

    private SecurityErrorWriter() {
    }

    static void escrever(HttpServletResponse response, int status, String code, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"code\":\"" + code + "\",\"message\":\"" + message.replace("\"", "\\\"") + "\"}");
    }
}
