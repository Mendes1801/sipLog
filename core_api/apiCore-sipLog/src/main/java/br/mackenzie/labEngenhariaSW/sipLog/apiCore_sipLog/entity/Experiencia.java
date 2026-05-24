package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

import org.hibernate.annotations.Formula;

import com.fasterxml.jackson.annotation.JsonCreator;


@Entity
@Table(name = "tb_experiencia")
@Data //Cria os getters e setters automaticamente
public class Experiencia {

    public enum Visibilidade {
        PUBLICA,
        AMIGOS,
        PRIVADA;

        @JsonCreator
        public static Visibilidade fromString(String value) {
            if (value == null) return null;
            
            String normalizado = value.trim().toUpperCase();
            
            // Mapeamento resiliente para evitar quebras por gênero gramatical ou digitação
            if ("PUBLICA".equals(normalizado) || "PUBLICO".equals(normalizado)) {
                return PUBLICA;
            }
            if ("AMIGOS".equals(normalizado) || "AMIGAS".equals(normalizado)) {
                return AMIGOS;
            }
            if ("PRIVADA".equals(normalizado) || "PRIVADO".equals(normalizado)) {
                return PRIVADA;
            }
            
            // Mensagem limpa indicando quais opções estão configuradas no ecossistema
            throw new IllegalArgumentException("Visibilidade inválida: " + value + 
                ". Valores aceitos: PUBLICA, AMIGOS, PRIVADA");
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bebida_id", nullable = false)
    private Bebida bebida;

    @Enumerated(EnumType.STRING)
    private Visibilidade visibilidade; // Visibilidade enum

    private Double nota;
    private String comentario;
    private String fotoPostUrl;
    private String localizacao;

    @Column(updatable = false)
    private LocalDateTime dataCriacao;
    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
    }

    // Conta quantas curtidas estão ligadas ao ID deste post.
    @Formula("(SELECT COUNT(c.id) FROM tb_curtida c WHERE c.experiencia_id = id)")
    private Integer totalCurtidas;

    // Conta quantos comentários estão ligados ao ID deste post.
    @Formula("(SELECT COUNT(co.id) FROM tb_comentario co WHERE co.experiencia_id = id)")
    private Integer totalComentarios;

    

}