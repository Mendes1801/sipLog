package br.mackenzie.labEngenhariaSW.sipLog.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import br.mackenzie.labEngenhariaSW.sipLog.entity.Experiencia;
import java.util.List;

public interface ExperienciaRepository extends JpaRepository<Experiencia, Long> {
    
    // Esse método vai buscar as experiências e já trazer o Usuário e a Bebida juntos 
    // em uma única query (resolve o problema de lentidão de N+1)
    @EntityGraph(attributePaths = {"usuario", "bebida"})
    List<Experiencia> findAllByOrderByDataCriacaoDesc();

    @EntityGraph(attributePaths = {"usuario", "bebida"})
    List<Experiencia> findByUsuarioIdOrderByDataCriacaoDesc(Long usuarioId);
}