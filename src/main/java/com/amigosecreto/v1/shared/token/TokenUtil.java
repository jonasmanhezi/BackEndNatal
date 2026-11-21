package com.amigosecreto.v1.shared.token;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

/** Tokens pessoais: 32 caracteres base36 (~165 bits) e hash SHA-256 para guardar no banco. */
public final class TokenUtil {

    public static final int TAMANHO_TOKEN = 32;

    private static final String ALFABETO = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private TokenUtil() {
    }

    public static String gerarToken() {
        StringBuilder sb = new StringBuilder(TAMANHO_TOKEN);
        for (int i = 0; i < TAMANHO_TOKEN; i++) {
            sb.append(ALFABETO.charAt(RANDOM.nextInt(ALFABETO.length())));
        }
        return sb.toString();
    }

    public static String hash(String valor) {
        return HexFormat.of().formatHex(sha256(valor));
    }

    /** Compara em tempo constante (não vaza quantos caracteres batem). */
    public static boolean iguais(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        return MessageDigest.isEqual(sha256(a), sha256(b));
    }

    private static byte[] sha256(String valor) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(valor.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível", e);
        }
    }
}
