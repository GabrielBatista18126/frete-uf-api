package com.gabrielbatista.freteapi.dto;

import java.math.BigDecimal;

public class FreteResponseDTO {
    private Long idFrete;
    private Short ufId;
    private String ufSigla;
    private String ufNome;
    private BigDecimal valor;

    public FreteResponseDTO() {}

    public FreteResponseDTO(Long idFrete, Short ufId, String ufSigla, String ufNome, BigDecimal valor) {
        this.idFrete = idFrete;
        this.ufId = ufId;
        this.ufSigla = ufSigla;
        this.ufNome = ufNome;
        this.valor = valor;
    }

    public Long getIdFrete() { return idFrete; }
    public void setIdFrete(Long idFrete) { this.idFrete = idFrete; }
    public Short getUfId() { return ufId; }
    public void setUfId(Short ufId) { this.ufId = ufId; }
    public String getUfSigla() { return ufSigla; }
    public void setUfSigla(String ufSigla) { this.ufSigla = ufSigla; }
    public String getUfNome() { return ufNome; }
    public void setUfNome(String ufNome) { this.ufNome = ufNome; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
}
