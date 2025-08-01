package com.gabrielbatista.freteapi.service;

import com.gabrielbatista.freteapi.exception.FreteNaoEncontradoException;
import com.gabrielbatista.freteapi.model.Frete;
import com.gabrielbatista.freteapi.repository.FreteRepository;
import com.gabrielbatista.freteapi.exception.UfDuplicadaException;
import com.gabrielbatista.freteapi.exception.UfNaoEncontradaException;
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
        String uf = frete.getUf();

        if (uf == null || uf.length() != 2) {
            throw new IllegalArgumentException("UF deve ter exatamente 2 caracteres.");
        }

        if (freteRepository.existsByUf(uf)) {
            throw new UfDuplicadaException(uf);
        }

        return freteRepository.save(frete);
    }
    public List<Frete> listarTodos() {
        return freteRepository.findAll();
    }

    public Frete buscarPorUf(String uf) {
        Optional<Frete> resultado = freteRepository.findByUf(uf.toUpperCase());

        if (resultado.isPresent()) {
            return resultado.get();
        } else {
            throw new UfNaoEncontradaException(uf);
        }
    }

    public void deletarPorId(Long id) {
        freteRepository.deleteById(id);
    }

    public Frete atualizarFrete(Long id, Frete novoFrete) {
        if (novoFrete.getUf() == null || novoFrete.getUf().length() != 2) {
            throw new IllegalArgumentException("UF deve ter exatamente 2 caracteres.");
        }
        Optional<Frete> freteOptional = freteRepository.findById(id);
        if (freteOptional.isPresent()) {
            Frete frete = freteOptional.get();
            frete.setUf(novoFrete.getUf());
            frete.setValor(novoFrete.getValor());
            return freteRepository.save(frete);
        } else {
            throw new FreteNaoEncontradoException(id);
        }
    }


}
