package com.amigosecreto.v1.shared.token;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenUtilTest {

    @Test
    void gerarToken_tem32CaracteresBase36_eNaoRepete() {
        Set<String> tokens = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            String token = TokenUtil.gerarToken();
            assertEquals(TokenUtil.TAMANHO_TOKEN, token.length());
            assertTrue(token.matches("[a-z0-9]+"));
            tokens.add(token);
        }
        assertEquals(1000, tokens.size());
    }

    @Test
    void hash_sha256Hex_deterministico() {
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", TokenUtil.hash("abc"));
        assertNotEquals(TokenUtil.hash("abc"), TokenUtil.hash("abd"));
    }

    @Test
    void iguais_comparaChaves() {
        assertTrue(TokenUtil.iguais("chave-do-organizador", "chave-do-organizador"));
        assertFalse(TokenUtil.iguais("chave-do-organizador", "chave-errada"));
        assertFalse(TokenUtil.iguais("chave", null));
    }
}
