package com.amigosecreto.v1.shared.participante;

import java.text.Normalizer;
import java.util.Locale;

/** "  Dona  CÉLIA " e "dona celia" são a mesma pessoa. */
public final class NomeUtil {

    private NomeUtil() {
    }

    public static String normalizar(String nome) {
        if (nome == null) {
            return "";
        }
        String semAcento = Normalizer.normalize(nome, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        return semAcento.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
}
