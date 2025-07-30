package com.gabrielbatista.freteapi.exception;

public class UfNaoEncontradaException extends RuntimeException {
    public UfNaoEncontradaException(String uf) {
        super("UF não encontrada: " + uf.toUpperCase());
    }
}
