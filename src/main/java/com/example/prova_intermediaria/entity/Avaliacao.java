package com.example.prova_intermediaria.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.DateFormat;

@Entity
@Table(name = "avaliacoes")
@Getter
@Setter
@NoArgsConstructor
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String autor;

    private String conteudo;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Nota nota;

    @Column(nullable = false)
    private DateFormat dataAvaliacao;

    @Column(nullable = false)
    private boolean deletado = false;
}