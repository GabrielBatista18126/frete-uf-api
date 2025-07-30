package com.gabrielbatista.freteapi.controller;

import com.gabrielbatista.freteapi.dto.FreteResponseDTO;
import com.gabrielbatista.freteapi.model.Frete;
import com.gabrielbatista.freteapi.service.FreteService;
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
    public ResponseEntity<Frete> cadastrarFrete(@RequestBody Frete frete) {
        Frete salvo = freteService.cadastrarFrete(frete);
        return ResponseEntity.ok(salvo);
    }

    @GetMapping
    public ResponseEntity<List<Frete>> listarTodos() {
        return ResponseEntity.ok(freteService.listarTodos());
    }

    @GetMapping("/uf/{uf}")
    public ResponseEntity<FreteResponseDTO> buscarPorUf(@PathVariable String uf){
        Frete frete = freteService.buscarPorUf(uf);
        FreteResponseDTO dto = new FreteResponseDTO(frete.getUf(), frete.getValor());
        return ResponseEntity.ok(dto);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Frete> atualizar(@PathVariable Long id, @RequestBody Frete frete) {
        Frete atualizado = freteService.atualizarFrete(id, frete);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        freteService.deletarPorId(id);
        return ResponseEntity.noContent().build();
    }
}
