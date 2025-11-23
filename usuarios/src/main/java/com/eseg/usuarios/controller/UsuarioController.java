package com.eseg.usuarios.controller;

import com.eseg.usuarios.dto.*;
import com.eseg.usuarios.model.Usuario;
import com.eseg.usuarios.service.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    private RestTemplate restTemplate = new RestTemplate();

    // URLs base dos outros microsserviços, configuráveis via application.properties
    @Value("${campanha.service.url:http://localhost:8080}")
    private String campanhaServiceUrl;

    @Value("${comentario.service.url:http://localhost:8081}")
    private String comentarioServiceUrl;

    @Value("${pagamento.service.url:http://localhost:8082}")
    private String pagamentoServiceUrl;

    @Value("${recompensa.service.url:http://localhost:8083}")
    private String recompensaServiceUrl;

    public UsuarioController(UsuarioService usuarioService, RestTemplate restTemplate) {
        this.usuarioService = usuarioService;
        this.restTemplate = restTemplate;
    }

    // Listar todas as campanhas: retorna lista simples com objetos Usuário (não expandido)
    @GetMapping
    public List<Usuario> listarTodos() {
        return usuarioService.listarTodos();
    }

    // Buscar usuário detalhado por Id (com dados expandidos dos microsserviços)
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDetalhadoDTO> listarPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);

        // Buscar detalhes das campanhas - como List<CampanhaDTO>
        List<CampanhaDTO> campanhas = usuario.getIdCampanhasCriadas() == null ? List.of() :
                usuario.getIdCampanhasCriadas().stream()
                        .map(cid -> restTemplate.getForObject(campanhaServiceUrl + "/campanhas/" + cid + "/resumo", CampanhaDTO.class))
                        .collect(Collectors.toList());

        // Buscar detalhes dos comentários - como List<ComentarioDTO>
        List<ComentarioDTO> comentarios = usuario.getIdComentariosFeitos() == null ? List.of() :
                usuario.getIdComentariosFeitos().stream()
                        .map(cid -> restTemplate.getForObject(comentarioServiceUrl + "/comentarios/" + cid, ComentarioDTO.class))
                        .collect(Collectors.toList());

        // Buscar detalhes dos pagamentos - como List<PagamentoDTO>
        List<PagamentoDTO> pagamentos = usuario.getIdPagamentosFeitos() == null ? List.of() :
                usuario.getIdPagamentosFeitos().stream()
                        .map(pid -> restTemplate.getForObject(pagamentoServiceUrl + "/pagamentos/" + pid, PagamentoDTO.class))
                        .collect(Collectors.toList());

        // Buscar detalhes das recompensas - como List<RecompensaDTO>
        List<RecompensaDTO> recompensas = usuario.getIdRecompensasRecebidas() == null ? List.of() :
                usuario.getIdRecompensasRecebidas().stream()
                        .map(rid -> restTemplate.getForObject(recompensaServiceUrl + "/recompensas/" + rid, RecompensaDTO.class))
                        .collect(Collectors.toList());

        UsuarioDetalhadoDTO dto = new UsuarioDetalhadoDTO(
            usuario.getId(),
            usuario.getNome(),
            usuario.getTelefone(),
            usuario.getEmail(),
            campanhas,
            comentarios,
            pagamentos,
            recompensas
        );

        return ResponseEntity.ok(dto);
    }

    // Criar usuário
    @PostMapping
    public ResponseEntity<Usuario> criar(@RequestBody Usuario novo) {
        Usuario criado = usuarioService.criarUsuario(novo);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    // Editar usuário
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> editar(@PathVariable Long id, @RequestBody Usuario atualizado) {
        Usuario editado = usuarioService.editarUsuario(id, atualizado);
        return ResponseEntity.ok(editado);
    }

    // Deletar usuário
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/resumo")
    public ResponseEntity<Usuario> buscarResumo(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }
}
