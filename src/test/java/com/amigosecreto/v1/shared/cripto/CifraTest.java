package com.amigosecreto.v1.shared.cripto;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CifraTest {

    private static final String TOKEN = "8q45xeror1y8ed64pyk99x5npolqhsfl";

    @Test
    void parSecreto_idaEVolta_comOTokenCerto() {
        String cifrado = ParSecreto.cifrar(TOKEN, 1L, 42L);

        assertEquals(Optional.of(42L), ParSecreto.decifrar(TOKEN, 1L, cifrado));
        assertFalse(cifrado.contains("42"));
    }

    @Test
    void parSecreto_tokenErrado_naoAbre() {
        String cifrado = ParSecreto.cifrar(TOKEN, 1L, 42L);

        assertTrue(ParSecreto.decifrar("outro-token-qualquer-de-32-chars", 1L, cifrado).isEmpty());
        assertTrue(ParSecreto.decifrar(null, 1L, cifrado).isEmpty());
    }

    @Test
    void parSecreto_copiarParaOutraLinha_naoAbre() {
        // Trocar o valor de linha no banco não engana: o id de quem deu entra na autenticação.
        String cifrado = ParSecreto.cifrar(TOKEN, 1L, 42L);

        assertTrue(ParSecreto.decifrar(TOKEN, 2L, cifrado).isEmpty());
    }

    @Test
    void cifrar_mesmoValorDuasVezes_textoDiferente() {
        // IV aleatório: dois pares para a mesma pessoa não ficam iguais no banco.
        assertNotEquals(ParSecreto.cifrar(TOKEN, 1L, 42L), ParSecreto.cifrar(TOKEN, 1L, 42L));
    }

    @Test
    void decifrar_valorAlteradoOuLixo_vazio() {
        String cifrado = ParSecreto.cifrar(TOKEN, 1L, 42L);
        char[] c = cifrado.toCharArray();
        c[20] = c[20] == 'A' ? 'B' : 'A';

        assertTrue(ParSecreto.decifrar(TOKEN, 1L, new String(c)).isEmpty());
        assertTrue(ParSecreto.decifrar(TOKEN, 1L, "não-é-base64!").isEmpty());
        assertTrue(ParSecreto.decifrar(TOKEN, 1L, "").isEmpty());
    }

    @Test
    void tokenGuardado_abreSoComAAdminKey() {
        String cifrado = Cifra.cifrar("admin-key-longa", Cifra.PROPOSITO_TOKEN, TOKEN, "hash");

        assertEquals(Optional.of(TOKEN), Cifra.decifrar("admin-key-longa", Cifra.PROPOSITO_TOKEN, cifrado, "hash"));
        assertTrue(Cifra.decifrar("outra-admin-key", Cifra.PROPOSITO_TOKEN, cifrado, "hash").isEmpty());
        assertTrue(Cifra.decifrar("admin-key-longa", Cifra.PROPOSITO_PAR, cifrado, "hash").isEmpty());
    }
}
