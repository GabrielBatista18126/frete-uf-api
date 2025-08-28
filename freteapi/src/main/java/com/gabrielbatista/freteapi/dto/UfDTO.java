package com.gabrielbatista.freteapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UfDTO {
    private Short id;
    private String sigla;
    private String nome;

    public UfDTO(Short id, String sigla, String nome) {
    }
}
