package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Experiencia;

import java.util.List;

public interface ExperienciaRepository extends JpaRepository<Experiencia, Long> {
    
    // Esse método vai buscar as experiências e já trazer o Usuário e a Bebida juntos 
    // em uma única query (resolve o problema de lentidão de N+1)
    @EntityGraph(attributePaths = {"usuario", "bebida"})
    List<Experiencia> findAllByOrderByDataCriacaoDesc();

    @EntityGraph(attributePaths = {"usuario", "bebida"})
    List<Experiencia> findByUsuarioIdOrderByDataCriacaoDesc(Long usuarioId);

    // Busca todas as experiências ordenadas da mais recente para a mais antiga
    @EntityGraph(attributePaths = {"usuario", "bebida"}) // Evita N+1
    Page<Experiencia> findAllByOrderByDataCriacaoDesc(Pageable pageable);


    // Busca apenas os posts de um usuário específico
    @EntityGraph(attributePaths = {"usuario", "bebida"}) // Evita o N+1
    Page<Experiencia> findByUsuarioIdOrderByDataCriacaoDesc(Long usuarioId, Pageable pageable);

    // Busca as experiências onde o ID do autor esteja dentro da 
    // lista de pessoas que o meu usuário segue
    @EntityGraph(attributePaths = {"usuario", "bebida"}) // Mantém a performance evitando N+1
    @Query("SELECT e FROM Experiencia e " +
           "WHERE e.usuario.id IN " +
           "(SELECT s.seguido.id FROM Seguidor s WHERE s.seguidor.id = :meuUsuarioId) " +
           "ORDER BY e.dataCriacao DESC")
    Page<Experiencia> findFeedAmigos(@Param("meuUsuarioId") Long meuUsuarioId, Pageable pageable);
}