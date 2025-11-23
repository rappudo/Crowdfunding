package com.eseg.campanhas.controller;

import com.eseg.campanhas.dto.*;
import com.eseg.campanhas.model.Campanha;
import com.eseg.campanhas.service.CampanhaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/campanhas")
public class CampanhaController {

    private final CampanhaService campanhaService;

    private RestTemplate restTemplate = new RestTemplate();

    // URLs base dos outros microsserviços, configuráveis via application.properties
    @Value("${comentario.service.url:http://localhost:8081}")
    private String comentarioServiceUrl;

    @Value("${pagamento.service.url:http://localhost:8082}")
    private String pagamentoServiceUrl;

    @Value("${recompensa.service.url:http://localhost:8083}")
    private String recompensaServiceUrl;

    @Value("${usuario.service.url:http://localhost:8084}")
    private String usuarioServiceUrl;

    public CampanhaController(CampanhaService campanhaService, RestTemplate restTemplate) {
        this.campanhaService = campanhaService;
        this.restTemplate = restTemplate;
    }

    // Listar todas as campanhas: retorna lista simples com objetos Campanha (não expandido)
    @GetMapping
    public List<Campanha> listarTodas() {
        List<Campanha> campanhas = campanhaService.listarTodas();
        System.out.println("Quantidade campanhas encontradas: " + campanhas.size());
        return campanhas;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampanhaDetalhadaDTO> listarPorId(@PathVariable Long id) {
        Campanha campanha = campanhaService.buscarPorId(id);

        // 1. Comentários com proteção
        List<ComentarioDTO> comentarios = campanha.getIdComentarios() == null ? List.of() :
                campanha.getIdComentarios().stream()
                        .map(cid -> {
                            try {
                                return restTemplate.getForObject(comentarioServiceUrl + "/comentarios/" + cid, ComentarioDTO.class);
                            } catch (Exception e) { return null; } //
                        })
                        .filter(java.util.Objects::nonNull)
                        .collect(Collectors.toList());

        // 2. Pagamentos com proteção
        List<PagamentoDTO> pagamentos = campanha.getIdPagamentos() == null ? List.of() :
                campanha.getIdPagamentos().stream()
                        .map(pid -> {
                            try {
                                return restTemplate.getForObject(pagamentoServiceUrl + "/pagamentos/" + pid, PagamentoDTO.class);
                            } catch (Exception e) { return null; }
                        })
                        .filter(java.util.Objects::nonNull)
                        .collect(Collectors.toList());

        // 3. Recompensas com proteção
        List<RecompensaDTO> recompensas = campanha.getIdRecompensas() == null ? List.of() :
                campanha.getIdRecompensas().stream()
                        .map(rid -> {
                            try {
                                return restTemplate.getForObject(recompensaServiceUrl + "/recompensas/" + rid, RecompensaDTO.class);
                            } catch (Exception e) { return null; }
                        })
                        .filter(java.util.Objects::nonNull)
                        .collect(Collectors.toList());

        // 4. Usuário com proteção e correção de ID
        UsuarioDTO usuario;
        try {
            usuario = restTemplate.getForObject(usuarioServiceUrl + "/usuarios/" + campanha.getIdCriador(), UsuarioDTO.class);
        } catch (Exception e) {
            usuario = new UsuarioDTO();
            usuario.setNome("Desconhecido (Erro ao buscar)");
        }

        CampanhaDetalhadaDTO dto = new CampanhaDetalhadaDTO(
                campanha.getId(), campanha.getIdCriador(), campanha.getTitulo(), campanha.getDescricao(),
                campanha.getMeta(), campanha.getValorArrecadado(), campanha.getDataCriacao(),
                campanha.getDataEncerramento(), campanha.getStatus(),
                comentarios, pagamentos, recompensas, usuario
        );

        return ResponseEntity.ok(dto);
    }

    // Criar campanha
    @PostMapping
    public ResponseEntity<Campanha> criar(@RequestBody Campanha nova) {
        Campanha criada = campanhaService.criarCampanha(nova);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    // Editar campanha
    @PutMapping("/{id}")
    public ResponseEntity<Campanha> editar(@PathVariable Long id, @RequestBody Campanha atualizada) {
        Campanha editada = campanhaService.editarCampanha(id, atualizada);
        return ResponseEntity.ok(editada);
    }

    // Deletar campanha
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        campanhaService.deletarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/resumo")
    public ResponseEntity<Campanha> buscarResumo(@PathVariable Long id) {
        return ResponseEntity.ok(campanhaService.buscarPorId(id));
    }
}
