package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

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

}