package com.amigosecreto.v1.shared.exception;

/** Vira HTTP 404 (code "not_found"). */
public class NaoEncontradoException extends RuntimeException {

    public NaoEncontradoException(String message) {
        super(message);
    }
}
