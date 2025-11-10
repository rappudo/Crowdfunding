package com.eseg.pagamentos.controller;

import com.eseg.pagamentos.model.Pagamento;
import com.eseg.pagamentos.service.PagamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {
    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    // Listar todos os pagamentos
    @GetMapping
    public List<Pagamento> listarTodos() {
        return pagamentoService.listarTodos();
    }

    // Buscar pagamento por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pagamento> buscarPorId(@PathVariable Long id) {
        Pagamento pagamento = pagamentoService.buscarPorId(id);
        return pagamento != null ? ResponseEntity.ok(pagamento) : ResponseEntity.notFound().build();
    }

    // Criar novo pagamento
    @PostMapping
    public ResponseEntity<Pagamento> criar(@RequestBody Pagamento novoPagamento) {
        Pagamento criado = pagamentoService.criarPagamento(novoPagamento);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    // Atualizar pagamento existente
    @PutMapping("/{id}")
    public ResponseEntity<Pagamento> atualizar(@PathVariable Long id, @RequestBody Pagamento atualizacao) {
        Pagamento atualizado = pagamentoService.editarPagamento(id, atualizacao);
        return ResponseEntity.ok(atualizado);
    }

    // Remover pagamento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        pagamentoService.deletarPorId(id);
        return ResponseEntity.noContent().build();
    }
}
