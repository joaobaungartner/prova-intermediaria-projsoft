package com.example.prova_intermediaria.controller;

import com.example.prova_intermediaria.entity.Avaliacao;
import com.example.prova_intermediaria.service.AvaliacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/avaliacao")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @PostMapping
    public ResponseEntity<Avaliacao> criar(@RequestBody Avaliacao avaliacao) {
        Avaliacao criado = avaliacaoService.criar(avaliacao);

        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @GetMapping
    public List<Avaliacao> listar(
            @RequestParam(name = "nome", required = false) String nome) {
        return avaliacaoService.listar(nome);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable("id") Long id) {
        avaliacaoService.deletar(id);

        return ResponseEntity.noContent().build();
    }
}