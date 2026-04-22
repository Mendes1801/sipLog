package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

import org.hibernate.annotations.Formula;

@Entity
@Table(name = "tb_experiencia")
@Data //Cria os getters e setters automaticamente
public class Experiencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bebida_id", nullable = false)
    private Bebida bebida;

    private String visibilidade; // "PUBLICA", "AMIGOS", "PRIVADA"
    private Double nota;
    private String comentario;
    private String fotoPostUrl;
    private String localizacao;

    private LocalDateTime dataCriacao = LocalDateTime.now();

    // Conta quantas curtidas estão ligadas ao ID deste post.
    @Formula("(SELECT COUNT(c.id) FROM tb_curtida c WHERE c.experiencia_id = id)")
    private Integer totalCurtidas;

    // Conta quantos comentários estão ligados ao ID deste post.
    @Formula("(SELECT COUNT(co.id) FROM tb_comentario co WHERE co.experiencia_id = id)")
    private Integer totalComentarios;

}