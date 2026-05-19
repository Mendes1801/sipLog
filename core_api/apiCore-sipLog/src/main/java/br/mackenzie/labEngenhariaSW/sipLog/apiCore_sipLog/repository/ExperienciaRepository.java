package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Experiencia;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Experiencia.Visibilidade;

import java.util.List;

public interface ExperienciaRepository extends JpaRepository<Experiencia, Long> {
    
    // FEED GLOBAL
    //Vai buscar APENAS o que for de uma visibilidade específica (PUBLICA)
    @EntityGraph(attributePaths = {"usuario", "bebida"})
    Page<Experiencia> findByVisibilidadeOrderByDataCriacaoDesc(Visibilidade visibilidade, Pageable pageable);

    
    // MEU FEED
    // Busca TODOS os posts de um usuário específico
    @EntityGraph(attributePaths = {"usuario", "bebida"}) 
    Page<Experiencia> findByUsuarioIdOrderByDataCriacaoDesc(Long usuarioId, Pageable pageable);


    // FEED DE TERCEIROS (Visitando um perfil)
    // Usado quando visito o perfil de outra pessoa
    @EntityGraph(attributePaths = {"usuario", "bebida"}) 
    Page<Experiencia> findByUsuarioIdAndVisibilidadeInOrderByDataCriacaoDesc(Long usuarioId, List<Visibilidade> visibilidades, Pageable pageable);


    // FEED DE AMIGOS
    // Busca experiências de quem eu sigo, MAS agora filtra para não trazer as "PRIVADAS" deles.
    @EntityGraph(attributePaths = {"usuario", "bebida"}) 
    @Query("SELECT e FROM Experiencia e " +
           "WHERE e.usuario.id IN " +
           "(SELECT s.seguido.id FROM Seguidor s WHERE s.seguidor.id = :meuUsuarioId) " +
           "AND e.visibilidade IN :visibilidades " +
           "ORDER BY e.dataCriacao DESC")
    Page<Experiencia> findFeedAmigos(
            @Param("meuUsuarioId") Long meuUsuarioId, 
            @Param("visibilidades") List<Visibilidade> visibilidades, 
            Pageable pageable);


    // CÁLCULO DE MÉDIA
    @Query("SELECT AVG(e.nota) FROM Experiencia e WHERE e.bebida.id = :bebidaId")
    Double calcularMediaGlobalDaBebida(@Param("bebidaId") Long bebidaId);

    // Calcula a quantidade de experiências postadas por um usuário específico
    long countByUsuarioId(Long idUsuario);

    //Calcula a nota média global do usuário com base nas avaliações recebidas
    @Query("SELECT AVG(e.nota) FROM Experiencia e WHERE e.usuario.id = :idUsuario")
    Double findNotaMediaByUsuarioId(@Param("idUsuario") Long idUsuario);
}