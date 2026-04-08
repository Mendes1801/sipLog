package br.mackenzie.labEngenhariaSW.sipLog.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_usuario")
@Data //Cria os getters e setters automaticamente
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String keycloakId; // Para amarrar o usuário do Keycloak com o nosso banco

    private String nome;
    private String username;
    private String bio;
    private String fotoAvatarUrl;
}