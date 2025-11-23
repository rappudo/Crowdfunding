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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

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

    @MockitoBean
    private CampanhaRepository campanhaRepository;

    @MockitoBean
    private RestTemplate restTemplate;

    private Campanha campanhaPadrao;

    @BeforeEach
    void setUp() {
        campanhaPadrao = new Campanha(
                1L,
                100L,
                "Campanha de Teste",
                "Descricao da campanha de teste",
                new BigDecimal("5000.00"),
                new BigDecimal("100.00"),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                1
        );

        campanhaPadrao.setIdComentarios(new ArrayList<>(List.of(10L)));
        campanhaPadrao.setIdPagamentos(new ArrayList<>(List.of(20L)));
        campanhaPadrao.setIdRecompensas(new ArrayList<>(List.of(30L)));
    }

    @Test
    @DisplayName("GET /campanhas/{id} - Deve retornar JSON detalhado agregando dados externos")
    void deveRetornarCampanhaDetalhada() throws Exception {
        Mockito.when(campanhaRepository.findById(1L)).thenReturn(Optional.of(campanhaPadrao));

        // Mocks dos microsserviços externos
        //Criação do Mock de Comentários e determinação da resposta esperada
        ComentarioDTO comentarioMock = new ComentarioDTO(10L, "Ótima iniciativa!", LocalDateTime.now(), 1L, 50L);
        Mockito.when(restTemplate.getForObject(contains("/comentarios/10"), eq(ComentarioDTO.class)))
                .thenReturn(comentarioMock);

        //Criação do Mock de Pagamentos e determinação da resposta esperada
        PagamentoDTO pagamentoMock = new PagamentoDTO(20L, new BigDecimal("100.00"), 1L, LocalDateTime.now());
        Mockito.when(restTemplate.getForObject(contains("/pagamentos/20"), eq(PagamentoDTO.class)))
                .thenReturn(pagamentoMock);

        //Criação do Mock de Recompensa e determinação da resposta esperada
        RecompensaDTO recompensaMock = new RecompensaDTO(30L, "Brinde", "Um brinde legal", new BigDecimal("50.00"), 1L);
        Mockito.when(restTemplate.getForObject(contains("/recompensas/30"), eq(RecompensaDTO.class)))
                .thenReturn(recompensaMock);

        //Criação do Mock de Usuário e determinação da resposta esperada
        UsuarioDTO usuarioMock = new UsuarioDTO(100L, "Maria Criadora", "1199999999", "maria@email.com");
        Mockito.when(restTemplate.getForObject(contains("/usuarios/1"), eq(UsuarioDTO.class)))
                .thenReturn(usuarioMock);

        //Verificação da resposta
        mockMvc.perform(get("/campanhas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Campanha de Teste"))
                .andExpect(jsonPath("$.meta").value(5000.00))
                .andExpect(jsonPath("$.comentarios[0].texto").value("Ótima iniciativa!"))
                .andExpect(jsonPath("$.criador.nome").value("Maria Criadora"));
    }

    @Test
    @DisplayName("POST /campanhas - Deve criar uma campanha com sucesso")
    void deveCriarCampanha() throws Exception {
        //Criação de nova campanha
        Campanha novaCampanha = new Campanha();
        novaCampanha.setTitulo("Nova Campanha");
        novaCampanha.setMeta(new BigDecimal("2000.00"));

        Mockito.when(campanhaRepository.save(any(Campanha.class))).thenAnswer(invocation -> {
            Campanha c = invocation.getArgument(0);
            c.setId(2L);
            return c;
        });

        //Validando se a resposta foi criada
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
        //Busca todas as campanhas
        Mockito.when(campanhaRepository.findAll()).thenReturn(List.of(campanhaPadrao));

        //Verifica se todas as campanhas chegaram
        mockMvc.perform(get("/campanhas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Campanha de Teste"));
    }

    @Test
    @DisplayName("GET /campanhas/{id} - Deve retornar 404 se não existir")
    void deveRetornarErroSeNaoEncontrar() throws Exception {
        //Busca campanha inexistente
        Mockito.when(campanhaRepository.findById(999L)).thenReturn(Optional.empty());

        //Verifica se retornou erro
        mockMvc.perform(get("/campanhas/999"))
                .andExpect(status().isNotFound()); // Agora validamos o 404 diretamente
    }

    @Test
    @DisplayName("PUT /campanhas/{id} - Deve editar campanha com sucesso")
    void deveEditarCampanha() throws Exception {
        //Dados para atualiar campanha
        Campanha campanhaAtualizada = new Campanha();
        campanhaAtualizada.setTitulo("Título Atualizado");
        campanhaAtualizada.setDescricao("Nova descrição");
        campanhaAtualizada.setMeta(new BigDecimal("8000.00"));

        //Resposta esperada
        Campanha campanhaRetornada = new Campanha(
                1L, 100L, "Título Atualizado", "Nova descrição",
                new BigDecimal("8000.00"), new BigDecimal("100.00"),
                LocalDateTime.now(), LocalDateTime.now().plusDays(30), 1
        );

        Mockito.doNothing().when(campanhaRepository).update(eq(1L), any(Campanha.class));
        Mockito.when(campanhaRepository.findById(1L)).thenReturn(Optional.of(campanhaRetornada));

        //Verificação de sucesso
        mockMvc.perform(put("/campanhas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campanhaAtualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Título Atualizado"));
    }

    @Test
    @DisplayName("PUT /campanhas/{id} - Deve retornar 404 ao tentar editar inexistente")
    void deveFalharAoEditarInexistente() throws Exception {
        Campanha campanhaAtualizada = new Campanha();
        campanhaAtualizada.setTitulo("Título Atualizado");

        // Simulamos o erro 404 vindo do Repositório/Service
        Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Campanha não encontrada"))
                .when(campanhaRepository).update(eq(999L), any(Campanha.class));

        //Verificaçã da resposta de Erro
        mockMvc.perform(put("/campanhas/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campanhaAtualizada)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /campanhas/{id} - Deve deletar campanha com sucesso")
    void deveDeletarCampanha() throws Exception {
        Mockito.doNothing().when(campanhaRepository).deleteById(1L);

        //Verifica se a campanha foi excluída
        mockMvc.perform(delete("/campanhas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /campanhas/{id} - Deve retornar 404 ao deletar inexistente")
    void deveFalharAoDeletarInexistente() throws Exception {
        // Simulamos o erro 404 vindo do Repositório/Service
        Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Campanha não encontrada"))
                .when(campanhaRepository).deleteById(999L);

        //Verifica status de erro
        mockMvc.perform(delete("/campanhas/999"))
                .andExpect(status().isNotFound());
    }
}