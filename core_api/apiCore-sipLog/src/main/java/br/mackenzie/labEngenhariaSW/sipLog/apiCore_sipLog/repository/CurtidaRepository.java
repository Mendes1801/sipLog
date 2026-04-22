package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Curtida;

public interface CurtidaRepository extends JpaRepository<Curtida, Long> {
    

    boolean existsByExperienciaIdAndUsuarioId(Long experienciaId, Long usuarioId);

    @Query("SELECT c.experiencia.id FROM Curtida c WHERE c.usuario.id = :usuarioId AND c.experiencia.id IN :experienciaIds")
    Set<Long> findExperienciasCurtidasPeloUsuario(
            @Param("usuarioId") Long usuarioId, 
            @Param("experienciaIds") List<Long> experienciaIds
    );

}
