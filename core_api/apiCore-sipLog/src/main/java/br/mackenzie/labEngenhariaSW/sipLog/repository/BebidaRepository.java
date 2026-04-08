package br.mackenzie.labEngenhariaSW.sipLog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.mackenzie.labEngenhariaSW.sipLog.entity.Bebida;

public interface BebidaRepository extends JpaRepository<Bebida, Long> {
    // espaço criar buscas customizadas, ex: buscar por nome
    // List<Bebida> findByNomeContainingIgnoreCase(String nome);
}