package br.mackenzie.labEngenhariaSW.sipLog.apiCore_sipLog.entity;

import jakarta.persistence.*;
import lombok.Data;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.Map;

@Entity
@Table(name = "tb_bebida")
@Data //Cria os getters e setters automaticamente
public class Bebida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String categoria; // TINTO, BRANCO, IPA, STOUT, etc.
    private String fabricante;

    // Salva JSON nativo no banco
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> caracteristicas;


}