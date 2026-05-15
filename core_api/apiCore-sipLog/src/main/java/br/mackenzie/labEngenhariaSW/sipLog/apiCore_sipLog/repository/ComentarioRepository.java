package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Comentario;

public interface ComentarioRepository extends JpaRepository<Comentario, Long>{

    Page<Comentario> findByExperienciaIdOrderByDataCriacaoAsc(Long idPost, Pageable paginacao);

}
