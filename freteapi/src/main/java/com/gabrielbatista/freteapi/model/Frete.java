package com.gabrielbatista.freteapi.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(
        name = "frete",
        uniqueConstraints = @UniqueConstraint(name = "uk_frete_uf", columnNames = "uf_id")
)
public class Frete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "uf_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_frete_uf")
    )
    private Uf uf;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    public Frete() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Uf getUf() { return uf; }
    public void setUf(Uf uf) { this.uf = uf; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
}
