package com.gabrielbatista.freteapi.repository;

import com.gabrielbatista.freteapi.model.Frete;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FreteRepository extends JpaRepository<Frete, Long> {

    Optional<Frete> findByUf(String uf);

    boolean existsByUf(String uf);

}
