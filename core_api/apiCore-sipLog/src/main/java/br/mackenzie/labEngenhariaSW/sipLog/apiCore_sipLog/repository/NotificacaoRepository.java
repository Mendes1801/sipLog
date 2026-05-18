package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity.Notificacao;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    
    // Traz a lista do mais novo para o mais antigo, carregando os dados do Ator para evitar N+1
    @EntityGraph(attributePaths = {"ator"})
    Page<Notificacao> findByRecebedorIdOrderByDataCriacaoDesc(Long recebedorId, Pageable pageable);

    // Método ultra-rápido para contar a bolinha vermelha
    long countByRecebedorIdAndLidaFalse(Long recebedorId);
}