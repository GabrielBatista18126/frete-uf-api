package com.gabrielbatista.freteapi.controller;

import com.gabrielbatista.freteapi.dto.FreteRequestDTO;
import com.gabrielbatista.freteapi.dto.FreteResponseDTO;
import com.gabrielbatista.freteapi.model.Frete;
import com.gabrielbatista.freteapi.service.FreteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/fretes")
public class FreteController {

    private final FreteService freteService;

    public FreteController(FreteService freteService) {
        this.freteService = freteService;
    }

    @PostMapping
    public ResponseEntity<FreteResponseDTO> cadastrarFrete(@RequestBody @Valid FreteRequestDTO dto) {
        Frete frete = new Frete();
        frete.setUf(dto.getUf()); // O DTO já valida o tamanho
        frete.setValor(dto.getValor());

        Frete salvo = freteService.cadastrarFrete(frete);

        return ResponseEntity.ok(new FreteResponseDTO(salvo.getUf(), salvo.getValor()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FreteResponseDTO> atualizarFrete(@PathVariable Long id, @RequestBody @Valid FreteRequestDTO dto) {
        Frete frete = new Frete();
        frete.setUf(dto.getUf());
        frete.setValor(dto.getValor());

        Frete atualizado = freteService.atualizarFrete(id, frete);

        return ResponseEntity.ok(new FreteResponseDTO(atualizado.getUf(), atualizado.getValor()));
    }

    @GetMapping
    public ResponseEntity<List<FreteResponseDTO>> listarTodos() {
        List<Frete> fretes = freteService.listarTodos();

        List<FreteResponseDTO> dtos = fretes.stream()
                .map(f -> new FreteResponseDTO(f.getUf(), f.getValor()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/uf/{uf}")
    public ResponseEntity<FreteResponseDTO> buscarPorUf(@PathVariable String uf) {
        Frete frete = freteService.buscarPorUf(uf);
        return ResponseEntity.ok(new FreteResponseDTO(frete.getUf(), frete.getValor()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        freteService.deletarPorId(id);
        return ResponseEntity.noContent().build();
    }
}
