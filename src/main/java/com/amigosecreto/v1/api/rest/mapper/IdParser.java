package com.amigosecreto.v1.api.rest.mapper;

import com.amigosecreto.v1.shared.constant.Commons;
import com.amigosecreto.v1.shared.exception.NaoEncontradoException;

/** O contrato usa ids como texto; no banco são números. */
public final class IdParser {

    private IdParser() {
    }

    public static Long parse(String id) {
        try {
            return Long.parseLong(id.trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new NaoEncontradoException(Commons.MSG_PESSOA_NAO_ENCONTRADA);
        }
    }
}
