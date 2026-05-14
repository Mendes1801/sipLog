package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Bebida;

public interface BebidaRepository extends JpaRepository<Bebida, Long> {

    List<Bebida> findTop10ByNomeContainingIgnoreCase(String nome);

}