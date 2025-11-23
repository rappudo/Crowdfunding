package com.eseg.pagamentos.controller;

import com.eseg.pagamentos.dto.RecompensaDTO;
import com.eseg.pagamentos.model.Pagamento;
import com.eseg.pagamentos.service.PagamentoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.List;

@RestController
@RequestMapping("/pagamentos")
@CrossOrigin("*")
public class PagamentoController {
    private final PagamentoService pagamentoService;
    private final RestTemplate restTemplate;

    @Value("${campanha.service.url:http://localhost:8080}")
    private String campanhaServiceUrl;

    @Value("${recompensa.service.url:http://localhost:8083}")
    private String recompensaServiceUrl;
    
    @Value("${usuario.service.url:http://localhost:8084}")
    private String usuarioServiceUrl;


    public PagamentoController(PagamentoService pagamentoService, RestTemplate restTemplate) {
        this.pagamentoService = pagamentoService;
        this.restTemplate = restTemplate;
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
    public ResponseEntity<?> criar(@RequestBody Pagamento novoPagamento) {
        try {
            try {
                restTemplate.postForEntity(
                    campanhaServiceUrl + "/campanhas/" + novoPagamento.getIdCampanha() + "/doar",
                    novoPagamento.getValor(),
                    Object.class
                );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Não foi possível processar a doação na campanha. Verifique se ela esta ativa");
        }

        Pagamento criado = pagamentoService.criarPagamento(novoPagamento);

        try {
            RecompensaDTO[] todasRecompensas = restTemplate.getForObject(
                recompensServiceUrl + "/recompensas",
                RecompensaDTO[].class
                );

            if (todasRecompensas != null) {
                Arrays.stream(todasRecompensas)
                    .filter(r -> r.getIdCampanha().equals(novoPagamento.getIdCampanha()))
                    .filter(r -> novoPagamento.getValor().compareTo(r.getValorMinimo()) >= 0)
                    .max((r1, r2) -> r1.getValorMinimo().compareTo(r2.getValorMinimo()))
                    .ifPresent(recompensaGanha -> {
                        Long idUsuario = novoPagamento.getIdUsuario();

                        restTemplate.postForEntity(
                            usuarioServiceUrl + "/usuarios/" + idUsuario + "/ganhar-recompensa/" + recompensGanha.getId(),
                            null,
                            Void.class
                        );
                        System.out.println("Recompensa " + recompensaGanha.getTitulo() + " atribuida!");
                    });
            }
        } catch (Exception e) {
            System.err.println("Erro ao processar recompensa: " + e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(criado);

      } catch (Exception e) {
          return ResponseEntity.internalServerError().body("Erro interno: " + e.getMessage());
      }
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
