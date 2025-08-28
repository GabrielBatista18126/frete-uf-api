package com.gabrielbatista.freteapi.controller;

import com.gabrielbatista.freteapi.dto.FreteRequestDTO;
import com.gabrielbatista.freteapi.dto.FreteResponseDTO;
import com.gabrielbatista.freteapi.model.Frete;
import com.gabrielbatista.freteapi.service.FreteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fretes")
public class FreteController {

    private final FreteService freteService;

    public FreteController(FreteService freteService) {
        this.freteService = freteService;
    }

    @PostMapping
    public ResponseEntity<FreteResponseDTO> cadastrarFrete(@RequestBody @Valid FreteRequestDTO dto) {
        FreteResponseDTO salvo = freteService.cadastrarFrete(dto);
        return ResponseEntity.ok(salvo);
    }

    // Atualiza por id do frete (mantida)
    @PutMapping("/{id}")
    public ResponseEntity<FreteResponseDTO> atualizarFrete(@PathVariable Long id,
                                                           @RequestBody @Valid FreteRequestDTO dto) {
        FreteResponseDTO atualizado = freteService.atualizarFrete(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @GetMapping
    public ResponseEntity<List<FreteResponseDTO>> listarTodos() {
        List<FreteResponseDTO> dtos = freteService.listarTodos()
                .stream()
                .map(f -> new FreteResponseDTO(
                        f.getId(),
                        f.getUf().getId(),
                        f.getUf().getSigla(),
                        f.getUf().getNome(),
                        f.getValor()))
                .toList();
        return ResponseEntity.ok(dtos);
    }

    // Busca por sigla (mantida para compatibilidade)
    @GetMapping("/uf/{uf}")
    public ResponseEntity<FreteResponseDTO> buscarPorUf(@PathVariable String uf) {
        Frete frete = freteService.buscarPorUfSigla(uf);
        return ResponseEntity.ok(new FreteResponseDTO(
                frete.getId(),
                frete.getUf().getId(),
                frete.getUf().getSigla(),
                frete.getUf().getNome(),
                frete.getValor()
        ));
    }

    // Nova: busca por ufId
    @GetMapping("/uf-id/{ufId}")
    public ResponseEntity<FreteResponseDTO> buscarPorUfId(@PathVariable Short ufId) {
        return ResponseEntity.ok(freteService.buscarPorUfId(ufId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        freteService.deletarPorId(id);
        return ResponseEntity.noContent().build();
    }
}
