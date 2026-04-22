package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_curtida", uniqueConstraints = {
    // Impede que o mesmo usuário curta o mesmo post duas vezes
    @UniqueConstraint(columnNames = {"usuario_id", "experiencia_id"})
})
@Getter
@Setter
public class Curtida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experiencia_id", nullable = false)
    private Experiencia experiencia;

    private LocalDateTime dataCriacao = LocalDateTime.now();
}