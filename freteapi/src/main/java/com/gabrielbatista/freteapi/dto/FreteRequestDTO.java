package com.gabrielbatista.freteapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class FreteRequestDTO {

    private Short ufId;

    @Size(min = 2, max = 2, message = "UF deve ter 2 letras")
    private String ufSigla;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal valor;

    public FreteRequestDTO() {}

    public Short getUfId() { return ufId; }
    public void setUfId(Short ufId) { this.ufId = ufId; }
    public String getUfSigla() { return ufSigla; }
    public void setUfSigla(String ufSigla) { this.ufSigla = ufSigla; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
}
