package com.example.prova_intermediaria.service;

import com.example.prova_intermediaria.entity.Avaliacao;
import com.example.prova_intermediaria.entity.Nota;
import com.example.prova_intermediaria.repository.AvaliacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class AvaliacaoServiceTest {

    private AvaliacaoRepository avaliacaoRepository;
    private AvaliacaoService avaliacaoService;

    @BeforeEach
    void configurar() {
        avaliacaoRepository = mock(AvaliacaoRepository.class);
        avaliacaoService = new AvaliacaoService(avaliacaoRepository);
    }

    @Test
    void deveCriarAvaliacaoSemIdEComDeletadoFalse() {
        Avaliacao entrada = new Avaliacao();
        entrada.setId(99L);
        entrada.setAutor("Java básico");
        entrada.setConteudo("Introdução ao Java");
        entrada.setDeletado(true);
        entrada.setNota(Nota.CINCO);
        entrada.setDataAvaliacao(LocalDate.of(2026, 9, 23));

        Avaliacao salvo = new Avaliacao();
        salvo.setId(1L);
        salvo.setAutor("Java básico");
        salvo.setConteudo("Introdução ao Java");
        salvo.setNota(Nota.CINCO);
        salvo.setDataAvaliacao(LocalDate.of(2026, 9, 23));

        when(avaliacaoRepository.save(entrada)).thenReturn(salvo);

        Avaliacao resultado = avaliacaoService.criar(entrada);

        assertNull(entrada.getId());
        assertFalse(entrada.isDeletado());
        assertEquals("Java básico", entrada.getAutor());
        assertEquals("Introdução ao Java", entrada.getConteudo());
        assertEquals(Nota.CINCO, entrada.getNota());
        assertEquals(LocalDate.of(2026, 9, 23), entrada.getDataAvaliacao());
        assertSame(salvo, resultado);

        verify(avaliacaoRepository).save(entrada);
        verifyNoMoreInteractions(avaliacaoRepository);
    }

    @Test
    void deveListarQuandoNomeForNull() {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setAutor("Java básico");
        List<Avaliacao> esperados = List.of(avaliacao);

        when(avaliacaoRepository.findByDeletadoFalse())
                .thenReturn(esperados);

        List<Avaliacao> resultado = avaliacaoService.listar(null);

        assertEquals(esperados, resultado);

        verify(avaliacaoRepository).findByDeletadoFalse();
        verifyNoMoreInteractions(avaliacaoRepository);
    }

    @Test
    void deveListarQuandoNomeForVazio() {
        when(avaliacaoRepository.findByDeletadoFalse())
                .thenReturn(List.of());

        List<Avaliacao> resultado = avaliacaoService.listar("");

        assertTrue(resultado.isEmpty());

        verify(avaliacaoRepository).findByDeletadoFalse();
        verifyNoMoreInteractions(avaliacaoRepository);
    }

    @Test
    void deveListarComFiltroPeloInicioDoNome() {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setAutor("Java básico");
        List<Avaliacao> esperados = List.of(avaliacao);

        when(avaliacaoRepository.findByNomeStartingWithAndDeletadoFalse("Java"))
                .thenReturn(esperados);

        List<Avaliacao> resultado = avaliacaoService.listar("Java");

        assertEquals(esperados, resultado);

        verify(avaliacaoRepository)
                .findByNomeStartingWithAndDeletadoFalse("Java");
        verifyNoMoreInteractions(avaliacaoRepository);
    }

    @Test
    void deveDeletarAvaliacaoLogicamente() {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setId(1L);
        avaliacao.setAutor("Java básico");
        avaliacao.setDeletado(false);

        when(avaliacaoRepository.findById(1L))
                .thenReturn(Optional.of(avaliacao));

        avaliacaoService.deletar(1L);

        assertTrue(avaliacao.isDeletado());

        verify(avaliacaoRepository).findById(1L);
        verify(avaliacaoRepository).save(avaliacao);
        verifyNoMoreInteractions(avaliacaoRepository);
    }

    @Test
    void deveRetornar404AoDeletarAvaliacaoInexistente() {
        when(avaliacaoRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> avaliacaoService.deletar(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());

        verify(avaliacaoRepository).findById(99L);
        verifyNoMoreInteractions(avaliacaoRepository);
    }
}
