package com.amigosecreto.v1.shared.exception;

/** Vira HTTP 413 (code "too_large"). */
public class ArquivoGrandeDemaisException extends RuntimeException {

    public ArquivoGrandeDemaisException(String message) {
        super(message);
    }
}
