package com.amigosecreto.v1.shared.cripto;

import java.util.Optional;

/** Quem tirou quem, cifrado com o token pessoal de quem deu. Nem o dono do banco lê. */
public final class ParSecreto {

    private ParSecreto() {
    }

    public static String cifrar(String tokenDeQuemDa, Long giverId, Long receiverId) {
        return Cifra.cifrar(tokenDeQuemDa, Cifra.PROPOSITO_PAR, String.valueOf(receiverId), contexto(giverId));
    }

    public static Optional<Long> decifrar(String tokenDeQuemDa, Long giverId, String receiverCifrado) {
        return Cifra.decifrar(tokenDeQuemDa, Cifra.PROPOSITO_PAR, receiverCifrado, contexto(giverId))
                .flatMap(ParSecreto::parseId);
    }

    private static String contexto(Long giverId) {
        return "giver:" + giverId;
    }

    private static Optional<Long> parseId(String valor) {
        try {
            return Optional.of(Long.parseLong(valor));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
