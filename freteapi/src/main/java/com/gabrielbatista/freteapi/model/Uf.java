package com.gabrielbatista.freteapi.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "ufs",
        uniqueConstraints = @UniqueConstraint(name = "uk_ufs_sigla", columnNames = "sigla")
)
public class Uf {

    @Id
    private Short id;

    @Column(nullable = false, length = 2)
    private String sigla;

    @Column(nullable = false, length = 50)
    private String nome;

    @Column(nullable = false)
    private Boolean ativo = true;

    public Uf() {}

    @PrePersist @PreUpdate
    void normalize() {
        if (this.sigla != null) this.sigla = this.sigla.trim().toUpperCase();
        if (this.nome != null) this.nome = this.nome.trim();
        if (this.ativo == null) this.ativo = true;
    }

    public Short getId() { return id; }
    public void setId(Short id) { this.id = id; }
    public String getSigla() { return sigla; }
    public void setSigla(String sigla) { this.sigla = sigla; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
