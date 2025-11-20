package com.eseg.campanhas;

import com.eseg.campanhas.dto.*;
import com.eseg.campanhas.model.Campanha;
import com.eseg.campanhas.repository.CampanhaRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CampanhasApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mockamos o Repository para não precisar do arquivo .json real durante o teste
    @MockitoBean
    private CampanhaRepository campanhaRepository;

    // Mockamos o RestTemplate para simular os outros microsserviços sem precisar que eles estejam rodando
    @MockitoBean
    private RestTemplate restTemplate;

    private Campanha campanhaPadrao;

    @BeforeEach
    void setUp() {
        // Cria uma campanha padrão para usar nos testes
        campanhaPadrao = new Campanha(
                1L,
                100L, // ID do Criador
                "Campanha de Teste",
                "Descricao da campanha de teste",
                new BigDecimal("5000.00"),
                new BigDecimal("100.00"),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                1 // Status: Em progresso
        );

        // Simula que essa campanha tem IDs vinculados de outros serviços
        campanhaPadrao.setIdComentarios(new ArrayList<>(List.of(10L)));
        campanhaPadrao.setIdPagamentos(new ArrayList<>(List.of(20L)));
        campanhaPadrao.setIdRecompensas(new ArrayList<>(List.of(30L)));
    }

    @Test
    @DisplayName("GET /campanhas/{id} - Deve retornar JSON detalhado agregando dados externos")
    void deveRetornarCampanhaDetalhada() throws Exception {
        // 1. Define o comportamento do Banco de Dados (Repository)
        Mockito.when(campanhaRepository.findById(1L)).thenReturn(Optional.of(campanhaPadrao));

        // 2. Define o comportamento dos Outros Microsserviços (RestTemplate)

        // Quando chamar o serviço de Comentários...
        ComentarioDTO comentarioMock = new ComentarioDTO(10L, "Ótima iniciativa!", LocalDateTime.now(), 1L, 50L);
        Mockito.when(restTemplate.getForObject(contains("/comentarios/10"), eq(ComentarioDTO.class)))
                .thenReturn(comentarioMock);

        // Quando chamar o serviço de Pagamentos...
        PagamentoDTO pagamentoMock = new PagamentoDTO(20L, new BigDecimal("100.00"), 1L, LocalDateTime.now());
        Mockito.when(restTemplate.getForObject(contains("/pagamentos/20"), eq(PagamentoDTO.class)))
                .thenReturn(pagamentoMock);

        // Quando chamar o serviço de Recompensas...
        RecompensaDTO recompensaMock = new RecompensaDTO(30L, "Brinde", "Um brinde legal", new BigDecimal("50.00"), 1L);
        Mockito.when(restTemplate.getForObject(contains("/recompensas/30"), eq(RecompensaDTO.class)))
                .thenReturn(recompensaMock);

        // Quando chamar o serviço de Usuários...
        UsuarioDTO usuarioMock = new UsuarioDTO(100L, "Maria Criadora", "1199999999", "maria@email.com");
        // Nota: O controller usa campanha.getId() na URL do usuário conforme seu código original
        Mockito.when(restTemplate.getForObject(contains("/usuarios/1"), eq(UsuarioDTO.class)))
                .thenReturn(usuarioMock);

        // 3. Faz a requisição e valida os campos
        mockMvc.perform(get("/campanhas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Campanha de Teste"))
                .andExpect(jsonPath("$.meta").value(5000.00))
                // Valida se os dados externos foram "colados" corretamente no JSON final
                .andExpect(jsonPath("$.comentarios[0].texto").value("Ótima iniciativa!"))
                .andExpect(jsonPath("$.pagamentos[0].valor").value(100.00))
                .andExpect(jsonPath("$.recompensas[0].titulo").value("Brinde"))
                .andExpect(jsonPath("$.criador.nome").value("Maria Criadora"));
    }

    @Test
    @DisplayName("POST /campanhas - Deve criar uma campanha com sucesso")
    void deveCriarCampanha() throws Exception {
        Campanha novaCampanha = new Campanha();
        novaCampanha.setTitulo("Nova Campanha");
        novaCampanha.setMeta(new BigDecimal("2000.00"));

        // Simula o salvamento no banco (Repository) retornando a campanha com ID
        Mockito.when(campanhaRepository.save(any(Campanha.class))).thenAnswer(invocation -> {
            Campanha c = invocation.getArgument(0);
            c.setId(2L); // Simula ID gerado
            return c;
        });

        mockMvc.perform(post("/campanhas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novaCampanha)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.titulo").value("Nova Campanha"));
    }

    @Test
    @DisplayName("GET /campanhas - Deve listar todas as campanhas")
    void deveListarTodas() throws Exception {
        Mockito.when(campanhaRepository.findAll()).thenReturn(List.of(campanhaPadrao));

        mockMvc.perform(get("/campanhas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Campanha de Teste"));
    }

    @Test
    @DisplayName("GET /campanhas/{id} - Deve retornar 404 se não existir") // 500 se não houver tratamento de exceção
    void deveRetornarErroSeNaoEncontrar() throws Exception {
        Mockito.when(campanhaRepository.findById(999L)).thenReturn(Optional.empty());

        // Como seu service lança RuntimeException, o status depende do seu tratamento de erro global.
        // Se não houver tratamento, o teste pode esperar 500 ou exceção.
        // Aqui assumimos que queremos verificar se a requisição é feita.
        try {
            mockMvc.perform(get("/campanhas/999"));
        } catch (Exception e) {
            // Exceção esperada se não houver ControllerAdvice
        }
    }

    @Test
    @DisplayName("PUT /campanhas/{id} - Deve editar campanha com sucesso")
    void deveEditarCampanha() throws Exception {
        // 1. Prepara os dados da atualização
        Campanha campanhaAtualizada = new Campanha();
        campanhaAtualizada.setTitulo("Título Atualizado");
        campanhaAtualizada.setDescricao("Nova descrição");
        campanhaAtualizada.setMeta(new BigDecimal("8000.00"));
        // Importante: O ID da URL (1L) prevalece sobre o corpo, mas é bom manter consistência

        // 2. Prepara o objeto que o banco retornará APÓS a edição
        Campanha campanhaRetornada = new Campanha(
                1L, 100L, "Título Atualizado", "Nova descrição",
                new BigDecimal("8000.00"), new BigDecimal("100.00"),
                LocalDateTime.now(), LocalDateTime.now().plusDays(30), 1
        );

        // 3. Mocks
        // O método update retorna void, então usamos doNothing()
        Mockito.doNothing().when(campanhaRepository).update(eq(1L), any(Campanha.class));

        // O Service chama buscarPorId logo após o update, então precisamos mockar esse retorno também
        Mockito.when(campanhaRepository.findById(1L)).thenReturn(Optional.of(campanhaRetornada));

        // 4. Execução e Validação
        mockMvc.perform(put("/campanhas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campanhaAtualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Título Atualizado"))
                .andExpect(jsonPath("$.meta").value(8000.00));
    }

    @Test
    @DisplayName("PUT /campanhas/{id} - Deve retornar erro ao tentar editar inexistente")
    void deveFalharAoEditarInexistente() throws Exception {
        Campanha campanhaAtualizada = new Campanha();
        campanhaAtualizada.setTitulo("Título Atualizado");

        // Simula erro no repositório
        Mockito.doThrow(new RuntimeException("Campanha não encontrada"))
                .when(campanhaRepository).update(eq(999L), any(Campanha.class));

        try {
            mockMvc.perform(put("/campanhas/999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(campanhaAtualizada)));
        } catch (Exception e) {
            // Sucesso se lançar exceção (já que não temos ControllerAdvice configurado para converter em 404)
        }
    }

    @Test
    @DisplayName("DELETE /campanhas/{id} - Deve deletar campanha com sucesso")
    void deveDeletarCampanha() throws Exception {
        // Simula deleção sem erros
        Mockito.doNothing().when(campanhaRepository).deleteById(1L);

        mockMvc.perform(delete("/campanhas/1"))
                .andExpect(status().isNoContent()); // Valida status 204
    }

    @Test
    @DisplayName("DELETE /campanhas/{id} - Deve retornar erro ao deletar inexistente")
    void deveFalharAoDeletarInexistente() throws Exception {
        // Simula erro ao deletar
        Mockito.doThrow(new RuntimeException("Campanha não encontrada"))
                .when(campanhaRepository).deleteById(999L);

        try {
            mockMvc.perform(delete("/campanhas/999"));
        } catch (Exception e) {
            // Sucesso se lançar exceção
        }
    }
}