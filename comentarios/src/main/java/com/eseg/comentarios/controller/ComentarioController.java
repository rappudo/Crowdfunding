package com.eseg.comentarios.controller;

import com.eseg.comentarios.dto.*;
import com.eseg.comentarios.model.Comentario;
import com.eseg.comentarios.service.ComentarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
public class ComentarioController {

    private final ComentarioService comentarioService;

    private final RestTemplate restTemplate = new RestTemplate();

    // URLs base dos outros microsserviços, configuráveis via application.properties
    @Value("${campanha.service.url}")
    private String comentarioServiceUrl;

    @Value("${pagamento.service.url}")
    private String pagamentoServiceUrl;

    @Value("${recompensa.service.url}")
    private String recompensaServiceUrl;

    @Value("${usuario.service.url}")
    private String usuarioServiceUrl;

    public ComentarioController(ComentarioService comentarioService) {
        this.comentarioService = comentarioService;
    }

    // Listar todas as campanhas: retorna lista simples com objetos Usuário (não expandido)
    @GetMapping
    public List<Comentario> listarTodos() {
        return comentarioService.comentarios();
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
