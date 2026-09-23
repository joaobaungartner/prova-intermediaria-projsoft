package com.example.prova_intermediaria.repository;

import com.example.prova_intermediaria.entity.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    List<Avaliacao> findByDeletadoFalse();

    List<Avaliacao> findByNomeStartingWithAndDeletadoFalse(String nome);
}
