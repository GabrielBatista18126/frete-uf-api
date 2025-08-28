package com.gabrielbatista.freteapi.repository;

import com.gabrielbatista.freteapi.model.Uf;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UfRepository extends JpaRepository<Uf, Short> {
    Optional<Uf> findBySigla(String sigla);
    List<Uf> findAllByAtivoTrueOrderBySiglaAsc();
}
