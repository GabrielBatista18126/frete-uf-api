package com.gabrielbatista.freteapi.exception;

public class UfDuplicadaException extends RuntimeException {

    public UfDuplicadaException(String uf) {
        super("UF já cadastrada: " + uf.toUpperCase());
    }

}