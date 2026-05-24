package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Seguidor;
import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Usuario;


public interface SeguidorRepository extends JpaRepository<Seguidor, Long> {

    // Conta quantas pessoas este usuário está seguindo
    long countBySeguidorId(Long seguidorId);

    // Conta quantos seguidores este usuário tem
    long countBySeguidoId(Long seguidoId);

    // Verifica se a relação já existe (para saber se pinta o botão de 'Seguindo' ou 'Seguir')
    boolean existsBySeguidorAndSeguido(Usuario seguidor, Usuario seguido);

    // Lista de seguidores de um usuário (paginada)
    Page<Usuario> findBySeguidoId(Long seguidoId, Pageable pageable);

    Optional<Seguidor> findBySeguidorIdAndSeguidoId(Long id, Long id2);

    boolean existsBySeguidorIdAndSeguidoId(Long id, Long idAlvo);

    @EntityGraph(attributePaths = {"seguido"})
    Page<Seguidor> findBySeguidorId(Long seguidorId, Pageable pageable);
}