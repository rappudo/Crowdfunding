package com.eseg.recompensas.controller;

import com.eseg.recompensas.model.Recompensa;
import com.eseg.recompensas.service.RecompensaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recompensas")
public class RecompensaController {
    private final RecompensaService recompensaService;

    public RecompensaController(RecompensaService recompensaService) {
        this.recompensaService = recompensaService;
    }

    //Listar todas as recompensas
    @GetMapping
    public List<Recompensa> listarTodas(){
        return recompensaService.listarTodos();
    }

    //Buscar recompensa por ID
    @GetMapping("/{id}")
    public ResponseEntity<Recompensa> buscarPorId(@PathVariable Long id) {
        Recompensa recompensa = recompensaService.buscaPorId(id);
        return recompensa != null ? ResponseEntity.ok(recompensa) : ResponseEntity.notFound().build();
    }

    //Criar recompensa
    @PostMapping
    public ResponseEntity<Recompensa> criar(@RequestBody Recompensa novaRecompensa){
        Recompensa criada = recompensaService.criarRecompensa(novaRecompensa);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recompensa> atualizar(@PathVariable Long id, @RequestBody Recompensa recompensaAtualizada) {
        Recompensa atualizada = recompensaService.editarRecompensa(id, recompensaAtualizada);
        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id){
        recompensaService.deletarPorId(id);
        return ResponseEntity.noContent().build();
    }
}

