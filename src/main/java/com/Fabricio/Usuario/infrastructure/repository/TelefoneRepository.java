package com.Fabricio.Usuario.infrastructure.repository;

import com.Fabricio.aprendendospring.infrastructure.entity.Telefone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelefoneRepository extends JpaRepository<Telefone, Long> {
}
