package com.gabrielbatista.freteapi.service;

import com.gabrielbatista.freteapi.model.Frete;
import com.gabrielbatista.freteapi.repository.FreteRepository;
import com.gabrielbatista.freteapi.exception.UfDuplicadaException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FreteService {

    private final FreteRepository freteRepository;

    public FreteService(FreteRepository freteRepository) {
        this.freteRepository = freteRepository;
    }

    public Frete cadastrarFrete(Frete frete) {
        if (freteRepository.existsByUf(frete.getUf())) {
            throw new UfDuplicadaException(frete.getUf());
        }
        return freteRepository.save(frete);
    }

    public List<Frete> listarTodos() {
        return freteRepository.findAll();
    }

    public Optional<Frete> buscarPorUf(String uf) {
        return freteRepository.findByUf(uf.toUpperCase());
    }

    public void deletarPorId(Long id) {
        freteRepository.deleteById(id);
    }

    public Frete atualizarFrete(Long id, Frete novoFrete) {
        return freteRepository.findById(id)
                .map(frete -> {
                    frete.setUf(novoFrete.getUf());
                    frete.setValor(novoFrete.getValor());
                    return freteRepository.save(frete);
                })
                .orElseThrow(() -> new RuntimeException("Frete não encontrado com ID: " + id));
    }
}
