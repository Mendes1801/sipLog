package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Bebida;

public interface BebidaRepository extends JpaRepository<Bebida, Long> {

    List<Bebida> findTop10ByNomeContainingIgnoreCase(String nome);

    @Query("SELECT DISTINCT b.categoria FROM Bebida b WHERE b.categoria IS NOT NULL ORDER BY b.categoria ASC")
    List<String> findDistinctCategorias();

}