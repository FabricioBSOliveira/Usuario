package com.Fabricio.Usuario.infrastructure.repository;

import com.Fabricio.Usuario.infrastructure.entity.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnderecoRepository extends JpaRepository<Endereco,Long> {
}
