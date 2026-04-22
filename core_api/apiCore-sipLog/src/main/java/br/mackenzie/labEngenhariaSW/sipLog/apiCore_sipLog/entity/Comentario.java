package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_comentario")
@Getter
@Setter
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; // Quem comentou

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experiencia_id", nullable = false)
    private Experiencia experiencia; // Em qual post

    @Column(nullable = false, length = 500)
    private String texto;

    private LocalDateTime dataCriacao = LocalDateTime.now();
}