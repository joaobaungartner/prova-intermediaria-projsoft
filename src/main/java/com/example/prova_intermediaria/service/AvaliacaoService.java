package com.example.prova_intermediaria.service;

import com.example.prova_intermediaria.entity.Avaliacao;
import com.example.prova_intermediaria.repository.AvaliacaoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
    }

    public Avaliacao criar(Avaliacao avaliacao) {
        avaliacao.setId(null);
        avaliacao.setDeletado(false);

        return avaliacaoRepository.save(avaliacao);
    }

    public List<Avaliacao> listar(String autor) {
        if (autor == null || autor.isEmpty()) {
            return avaliacaoRepository.findByDeletadoFalse();
        }

        return avaliacaoRepository.findByNomeStartingWithAndDeletadoFalse(autor);
    }

    public void deletar(Long id) {
        Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Avaliacao não encontrado"));

        avaliacao.setDeletado(true);
        avaliacaoRepository.save(avaliacao);
    }
}
