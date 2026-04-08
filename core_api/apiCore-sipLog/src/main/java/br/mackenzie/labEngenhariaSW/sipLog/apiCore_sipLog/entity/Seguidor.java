package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_seguidor", uniqueConstraints = {
    // Garante que o Lucas não consiga seguir a Carolina duas vezes no banco
    @UniqueConstraint(columnNames = {"seguidor_id", "seguido_id"})
})
@Data //Cria os getters e setters automaticamente
public class Seguidor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // O usuário que CLICOU no botão de seguir
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seguidor_id", nullable = false)
    private Usuario seguidor;

    // O usuário que ESTÁ SENDO seguido (o alvo)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seguido_id", nullable = false)
    private Usuario seguido;

    private LocalDateTime dataCriacao = LocalDateTime.now();
}