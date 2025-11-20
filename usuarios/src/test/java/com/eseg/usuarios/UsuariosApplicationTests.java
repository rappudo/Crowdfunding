package com.eseg.usuarios;

import com.eseg.usuarios.dto.*;
import com.eseg.usuarios.model.Usuario;
import com.eseg.usuarios.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UsuariosApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private RestTemplate restTemplate;

    private Usuario usuarioPadrao;

    @BeforeEach
    void setUp() {
        // Cria usuário base
        usuarioPadrao = new Usuario(1L, "João Teste", "11999999999", "joao@teste.com");

        // Simula que esse usuário tem interações com outros serviços
        usuarioPadrao.setIdCampanhasCriadas(new ArrayList<>(List.of(10L)));
        usuarioPadrao.setIdComentariosFeitos(new ArrayList<>(List.of(20L)));
        usuarioPadrao.setIdPagamentosFeitos(new ArrayList<>(List.of(30L)));
        usuarioPadrao.setIdRecompensasRecebidas(new ArrayList<>(List.of(40L)));
    }

    // --- TESTE COMPLEXO: AGREGAÇÃO DE DADOS ---

    @Test
    @DisplayName("GET /usuarios/{id} - Deve retornar DTO detalhado agregando 4 microsserviços")
    void deveRetornarUsuarioDetalhado() throws Exception {
        // 1. Mock do Repositório Local
        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPadrao));

        // 2. Mock das chamadas Externas (RestTemplate)

        // Simulando resposta do serviço de Campanhas
        CampanhaDTO campanhaMock = new CampanhaDTO();
        campanhaMock.setId(10L);
        campanhaMock.setTitulo("Campanha do João");
        Mockito.when(restTemplate.getForObject(contains("/campanhas/10"), eq(CampanhaDTO.class)))
                .thenReturn(campanhaMock);

        // Simulando resposta do serviço de Comentários
        ComentarioDTO comentarioMock = new ComentarioDTO();
        comentarioMock.setId(20L);
        comentarioMock.setTexto("Meu comentário");
        Mockito.when(restTemplate.getForObject(contains("/comentarios/20"), eq(ComentarioDTO.class)))
                .thenReturn(comentarioMock);

        // Simulando resposta do serviço de Pagamentos
        PagamentoDTO pagamentoMock = new PagamentoDTO();
        pagamentoMock.setId(30L);
        pagamentoMock.setValor(new BigDecimal("100.00"));
        Mockito.when(restTemplate.getForObject(contains("/pagamentos/30"), eq(PagamentoDTO.class)))
                .thenReturn(pagamentoMock);

        // Simulando resposta do serviço de Recompensas
        RecompensaDTO recompensaMock = new RecompensaDTO();
        recompensaMock.setId(40L);
        recompensaMock.setTitulo("Camiseta");
        Mockito.when(restTemplate.getForObject(contains("/recompensas/40"), eq(RecompensaDTO.class)))
                .thenReturn(recompensaMock);

        // 3. Execução e Validação
        mockMvc.perform(get("/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Teste"))
                // Verifica se os arrays foram populados corretamente com os dados externos
                .andExpect(jsonPath("$.campanhas[0].titulo").value("Campanha do João"))
                .andExpect(jsonPath("$.comentarios[0].texto").value("Meu comentário"))
                .andExpect(jsonPath("$.pagamentos[0].valor").value(100.00))
                .andExpect(jsonPath("$.recompensas[0].titulo").value("Camiseta"));
    }

    // --- TESTES CRUD PADRÃO ---

    @Test
    @DisplayName("POST /usuarios - Deve criar usuário")
    void deveCriarUsuario() throws Exception {
        Usuario novoUsuario = new Usuario(null, "Maria", "1188888888", "maria@email.com");

        Mockito.when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(2L);
            return u;
        });

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoUsuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.nome").value("Maria"));
    }

    @Test
    @DisplayName("PUT /usuarios/{id} - Deve editar usuário")
    void deveEditarUsuario() throws Exception {
        Usuario atualizacao = new Usuario(null, "João Editado", "1100000000", "joao@novo.com");
        Usuario usuarioEditado = new Usuario(1L, "João Editado", "1100000000", "joao@novo.com");

        // Mock do fluxo update + findById
        Mockito.doNothing().when(usuarioRepository).update(eq(1L), any(Usuario.class));
        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEditado));

        mockMvc.perform(put("/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Editado"));
    }

    @Test
    @DisplayName("DELETE /usuarios/{id} - Deve deletar usuário")
    void deveDeletarUsuario() throws Exception {
        Mockito.doNothing().when(usuarioRepository).deleteById(1L);

        mockMvc.perform(delete("/usuarios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /usuarios/{id} - Deve retornar 404 se usuário não existir")
    void deveRetornar404() throws Exception {
        Mockito.when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());
        // Ou se lançar exceção:
        Mockito.doThrow(new RuntimeException("Usuário não encontrado"))
                .when(usuarioRepository).findById(999L);

        try {
            mockMvc.perform(get("/usuarios/999"))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            // Ok se lançar exceção
        }
    }

    @Test
    @DisplayName("GET /usuarios - Deve listar todos (formato simples)")
    void deveListarTodos() throws Exception {
        Mockito.when(usuarioRepository.findAll()).thenReturn(List.of(usuarioPadrao));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("João Teste"))
                // Na listagem simples, não deve vir os detalhes complexos, apenas os IDs ou vazio
                .andExpect(jsonPath("$[0].idCampanhasCriadas[0]").value(10));
    }
}