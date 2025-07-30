package com.gabrielbatista.freteapi.dto;

import java.math.BigDecimal;

public class FreteResponseDTO {

    private String uf;
    private BigDecimal valor;

    public FreteResponseDTO(String uf, BigDecimal valor) {
        this.uf = uf;
        this.valor = valor;
    }

    public String getUf() {
        return uf;
    }

    public BigDecimal getValor() {
        return valor;
    }
}
