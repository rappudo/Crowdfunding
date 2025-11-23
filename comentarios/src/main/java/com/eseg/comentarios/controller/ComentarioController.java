package com.eseg.comentarios.controller;

import com.eseg.comentarios.dto.*;
import com.eseg.comentarios.model.Comentario;
import com.eseg.comentarios.service.ComentarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comentarios")
@CrossOrigin("*")
public class ComentarioController {

    private final ComentarioService comentarioService;

    public ComentarioController(ComentarioService comentarioService) {
        this.comentarioService = comentarioService;
    }

    // Listar todas as campanhas: retorna lista simples com objetos Usuário (não expandido)
    @GetMapping
    public List<Comentario> listarTodos() {
        List<Comentario> comentarios = comentarioService.listarTodos();
        System.out.println("Quantidade de comentarios encontrados: " + comentarios.size());
        return comentarios;
    }

    // Buscar usuário detalhado por Id (com dados expandidos dos microsserviços)
    @GetMapping("/{id}")
    public ResponseEntity<Comentario> buscarPorId(@PathVariable Long id) {
        Comentario comentario = comentarioService.comentarioPorID(id);
        return comentario != null ? ResponseEntity.ok(comentario) : ResponseEntity.notFound().build();
    }

    // Criar usuário
    @PostMapping
    public ResponseEntity<Comentario> criar(@RequestBody Comentario novo) {
        Comentario criado = comentarioService.criarComentario(novo);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    // Editar usuário
    @PutMapping("/{id}")
    public ResponseEntity<Comentario> editar(@PathVariable Long id, @RequestBody Comentario atualizado) {
        Comentario editado = comentarioService.editarComentario(id, atualizado);
        return ResponseEntity.ok(editado);
    }

    // Deletar usuário
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        comentarioService.deletarPorId(id);
        return ResponseEntity.noContent().build();
    }
}
