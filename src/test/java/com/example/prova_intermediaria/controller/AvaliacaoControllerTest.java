package com.example.prova_intermediaria.controller;

import com.example.prova_intermediaria.entity.Avaliacao;
import com.example.prova_intermediaria.entity.Nota;
import com.example.prova_intermediaria.repository.AvaliacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AvaliacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @BeforeEach
    void limparBanco() {
        avaliacaoRepository.deleteAll();
    }

    @Test
    void deveCriarAvaliacao() throws Exception {
        mockMvc.perform(post("/avaliacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "autor": "Java básico",
                                    "conteudo": "Introdução ao Java",
                                    "nota": 5,
                                    "dataAvaliacao": "2026-09-23"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.autor").value("Java básico"))
                .andExpect(jsonPath("$.conteudo").value("Introdução ao Java"))
                .andExpect(jsonPath("$.nota").exists())
                .andExpect(jsonPath("$.dataAvaliacao").value("2026-09-23"))
                .andExpect(jsonPath("$.deletado").value(false));

        List<Avaliacao> avaliacoes = avaliacaoRepository.findAll();

        assertEquals(1, avaliacoes.size());

        Avaliacao salvo = avaliacoes.getFirst();

        assertNotNull(salvo.getId());
        assertEquals("Java básico", salvo.getAutor());
        assertEquals("Introdução ao Java", salvo.getConteudo());
        assertEquals(Nota.CINCO, salvo.getNota());
        assertEquals(LocalDate.of(2026, 9, 23), salvo.getDataAvaliacao());
        assertFalse(salvo.isDeletado());
    }

    @Test
    void deveListarSomenteAvaliacoesNaoDeletadas() throws Exception {
        salvarAvaliacao("João", "Java básico", Nota.CINCO,
                LocalDate.of(2026, 9, 23), false);

        salvarAvaliacao("Maria", "Python básico", Nota.QUATRO,
                LocalDate.of(2026, 9, 23), false);

        salvarAvaliacao("Pedro", "Avaliação deletada", Nota.TRES,
                LocalDate.of(2026, 9, 23), true);

        mockMvc.perform(get("/avaliacao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].autor").value(
                        containsInAnyOrder("João", "Maria")));
    }

    @Test
    void deveFiltrarPeloInicioDoAutorSemMostrarDeletados() throws Exception {
        salvarAvaliacao("João Silva", "Java básico", Nota.CINCO,
                LocalDate.of(2026, 9, 23), false);

        salvarAvaliacao("João Pedro", "Java avançado", Nota.QUATRO,
                LocalDate.of(2026, 9, 23), false);

        salvarAvaliacao("Maria", "Introdução ao Java", Nota.CINCO,
                LocalDate.of(2026, 9, 23), false);

        salvarAvaliacao("Pedro", "Python básico", Nota.TRES,
                LocalDate.of(2026, 9, 23), false);

        salvarAvaliacao("João deletado", "Java", Nota.DOIS,
                LocalDate.of(2026, 9, 23), true);

        mockMvc.perform(get("/avaliacao")
                        .param("autor", "João"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].autor").value(
                        containsInAnyOrder("João Silva", "João Pedro")));
    }

    private Avaliacao salvarAvaliacao(
            String autor,
            String conteudo,
            Nota nota,
            LocalDate dataAvaliacao,
            boolean deletado
    ) {
        Avaliacao avaliacao = new Avaliacao();

        avaliacao.setAutor(autor);
        avaliacao.setConteudo(conteudo);
        avaliacao.setNota(nota);
        avaliacao.setDataAvaliacao(dataAvaliacao);
        avaliacao.setDeletado(deletado);

        return avaliacaoRepository.save(avaliacao);
    }

    @Test
    void deveDeletarLogicamenteEEsconderDaListagem() throws Exception {
        Avaliacao avaliacao = salvarAvaliacao(
                "João",
                "Java básico",
                Nota.CINCO,
                LocalDate.of(2026, 9, 23),
                false
        );

        mockMvc.perform(delete("/avaliacao/{id}", avaliacao.getId()))
                .andExpect(status().isNoContent());

        Avaliacao salvo = avaliacaoRepository.findById(avaliacao.getId())
                .orElseThrow();

        assertTrue(salvo.isDeletado());
        assertEquals("João", salvo.getAutor());
        assertEquals("Java básico", salvo.getConteudo());
        assertEquals(1L, avaliacaoRepository.count());

        mockMvc.perform(get("/avaliacao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        mockMvc.perform(get("/avaliacao")
                        .param("autor", "João"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void deveRetornar404ParaIdInexistente() throws Exception {
        mockMvc.perform(delete("/avaliacao/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }
}