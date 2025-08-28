package com.gabrielbatista.freteapi.service;

import com.gabrielbatista.freteapi.dto.FreteRequestDTO;
import com.gabrielbatista.freteapi.dto.FreteResponseDTO;
import com.gabrielbatista.freteapi.exception.FreteNaoEncontradoException;
import com.gabrielbatista.freteapi.exception.UfNaoEncontradaException;
import com.gabrielbatista.freteapi.model.Frete;
import com.gabrielbatista.freteapi.model.Uf;
import com.gabrielbatista.freteapi.repository.FreteRepository;
import com.gabrielbatista.freteapi.repository.UfRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FreteService {

    private final FreteRepository freteRepository;
    private final UfRepository ufRepository;

    public FreteService(FreteRepository freteRepository, UfRepository ufRepository) {
        this.freteRepository = freteRepository;
        this.ufRepository = ufRepository;
    }

    /** Cria frete usando ufId (preferido) ou ufSigla (fallback). A unicidade fica por conta da UNIQUE(uf_id) no banco. */
    public FreteResponseDTO cadastrarFrete(FreteRequestDTO dto) {
        Uf uf = resolveUf(dto);

        Frete frete = new Frete();
        frete.setUf(uf);
        frete.setValor(dto.getValor());

        // Se já existir frete para a UF, o banco lança DataIntegrityViolationException (o handler converte para 409).
        Frete salvo = freteRepository.save(frete);
        return toDTO(salvo);
    }

    public List<Frete> listarTodos() {
        return freteRepository.findAll();
    }

    /** Busca por sigla (compatível com rota antiga /fretes/uf/{uf}). */
    public Frete buscarPorUfSigla(String ufSigla) {
        return freteRepository.findByUf_Sigla(ufSigla.trim().toUpperCase())
                .orElseThrow(() -> new UfNaoEncontradaException(ufSigla));
    }

    /** Atualiza por id do frete (apenas valor). */
    public FreteResponseDTO atualizarFrete(Long idFrete, FreteRequestDTO dto) {
        Frete frete = freteRepository.findById(idFrete)
                .orElseThrow(() -> new FreteNaoEncontradoException(idFrete));
        frete.setValor(dto.getValor());
        Frete atualizado = freteRepository.save(frete);
        return toDTO(atualizado);
    }

    public void deletarPorId(Long id) {
        freteRepository.deleteById(id);
    }

    /** Busca por id da UF (útil para telas com dropdown). */
    public FreteResponseDTO buscarPorUfId(Short ufId) {
        Frete f = freteRepository.findByUf_Id(ufId)
                .orElseThrow(() -> new UfNaoEncontradaException("id=" + ufId));
        return toDTO(f);
    }

    /** Resolve UF a partir do DTO. */
    private Uf resolveUf(FreteRequestDTO dto) {
        if (dto.getUfId() != null) {
            return ufRepository.findById(dto.getUfId())
                    .orElseThrow(() -> new UfNaoEncontradaException("id=" + dto.getUfId()));
        }
        if (dto.getUfSigla() != null && dto.getUfSigla().trim().length() == 2) {
            return ufRepository.findBySigla(dto.getUfSigla().trim().toUpperCase())
                    .orElseThrow(() -> new UfNaoEncontradaException(dto.getUfSigla()));
        }
        throw new IllegalArgumentException("Informe ufId ou ufSigla (2 letras).");
    }

    /** Mapeia entidade -> DTO para resposta. */
    private FreteResponseDTO toDTO(Frete e) {
        return new FreteResponseDTO(
                e.getId(),
                e.getUf().getId(),
                e.getUf().getSigla(),
                e.getUf().getNome(),
                e.getValor()
        );
    }
}
