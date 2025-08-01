package com.gabrielbatista.freteapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class FreteRequestDTO {

    @NotBlank(message = "UF não pode estar em branco.")
    @Size(min = 2, max = 2, message = "UF deve ter exatamente 2 caracteres.")
    private String uf;

    private BigDecimal valor;

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
