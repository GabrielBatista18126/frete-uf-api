package com.gabrielbatista.freteapi.exception;

public class FreteNaoEncontradoException extends RuntimeException{
    public FreteNaoEncontradoException(Long id){
        super("Frete não encontrado com ID: " + id);
    }
}
