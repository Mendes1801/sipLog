package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository;

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
}