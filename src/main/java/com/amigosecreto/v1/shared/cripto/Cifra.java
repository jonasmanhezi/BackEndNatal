package com.amigosecreto.v1.shared.cripto;

import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

/**
 * AES-256-GCM. A chave sai do SHA-256 de um segredo (o token pessoal ou a ADMIN_KEY) mais um propósito,
 * então quem só olha o banco vê texto embaralhado.
 * Formato salvo: base64(iv de 12 bytes + texto cifrado + tag).
 */
public final class Cifra {

    /** Par do sorteio, cifrado com o token de quem deu. */
    public static final String PROPOSITO_PAR = "par";

    /** Token pessoal guardado para o organizador reenviar, cifrado com a ADMIN_KEY. */
    public static final String PROPOSITO_TOKEN = "token";

    private static final int IV_BYTES = 12;
    private static final int TAG_BITS = 128;
    private static final SecureRandom RANDOM = new SecureRandom();

    private Cifra() {
    }

    public static String cifrar(String segredo, String proposito, String texto, String contexto) {
        try {
            byte[] iv = new byte[IV_BYTES];
            RANDOM.nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, chave(segredo, proposito), new GCMParameterSpec(TAG_BITS, iv));
            cipher.updateAAD(contexto.getBytes(StandardCharsets.UTF_8));
            byte[] cifrado = cipher.doFinal(texto.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(
                    ByteBuffer.allocate(iv.length + cifrado.length).put(iv).put(cifrado).array());
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Falha ao cifrar", e);
        }
    }

    /** Vazio se o segredo ou o contexto não batem (ou o valor foi alterado). */
    public static Optional<String> decifrar(String segredo, String proposito, String valor, String contexto) {
        if (segredo == null || segredo.isEmpty() || valor == null || valor.isEmpty()) {
            return Optional.empty();
        }
        try {
            byte[] tudo = Base64.getDecoder().decode(valor);
            if (tudo.length <= IV_BYTES) {
                return Optional.empty();
            }

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, chave(segredo, proposito),
                    new GCMParameterSpec(TAG_BITS, tudo, 0, IV_BYTES));
            cipher.updateAAD(contexto.getBytes(StandardCharsets.UTF_8));
            byte[] texto = cipher.doFinal(tudo, IV_BYTES, tudo.length - IV_BYTES);

            return Optional.of(new String(texto, StandardCharsets.UTF_8));
        } catch (AEADBadTagException | IllegalArgumentException e) {
            return Optional.empty();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Falha ao decifrar", e);
        }
    }

    private static SecretKeySpec chave(String segredo, String proposito) throws GeneralSecurityException {
        byte[] bytes = MessageDigest.getInstance("SHA-256")
                .digest(("amigo-secreto|" + proposito + "|" + segredo).getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(bytes, "AES");
    }
}
