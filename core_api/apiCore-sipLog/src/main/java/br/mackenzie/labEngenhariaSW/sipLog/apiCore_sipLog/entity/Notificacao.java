package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tb_notificacao")
@Data
public class Notificacao {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recebedor_id")
    private Usuario recebedor; // O dono da postagem ou perfil

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ator_id")
    private Usuario ator; // Quem curtiu, comentou ou seguiu

    @Enumerated(EnumType.STRING)
    private TipoNotificacao tipo;

    private Long referenciaId; // Pode ser o ID do Post. Se for NOVO_SEGUIDOR, pode ficar nulo.

    private boolean lida;

    @Column(updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
        this.lida = false; // Toda notificação nasce não lida
    }
}