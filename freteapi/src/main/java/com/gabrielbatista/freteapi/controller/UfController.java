package com.gabrielbatista.freteapi.controller;

import com.gabrielbatista.freteapi.dto.UfDTO;
import com.gabrielbatista.freteapi.repository.UfRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ufs")
public class UfController {

    private final UfRepository ufRepository;

    public UfController(UfRepository ufRepository) {
        this.ufRepository = ufRepository;
    }

    @GetMapping
    public List<UfDTO> listar() {
        return ufRepository.findAllByAtivoTrueOrderBySiglaAsc()
                .stream()
                .map(u -> new UfDTO(u.getId(), u.getSigla(), u.getNome()))
                .toList();
    }
}
