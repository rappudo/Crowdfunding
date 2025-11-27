package com.eseg.pagamentos;

import com.eseg.pagamentos.model.Pagamento;
import com.eseg.pagamentos.repository.PagamentoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PagamentosApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mockamos o repositório para não depender do arquivo JSON físico
    @MockitoBean
    private PagamentoRepository pagamentoRepository;

    @MockitoBean
    private RestTemplate restTemplate;

    private Pagamento pagamentoPadrao;

    @BeforeEach
    void setUp() {
        // Cria um Pagamento base para os testes
        pagamentoPadrao = new Pagamento(
                1L,
                new BigDecimal("150.00"),
                10L, // ID da Campanha
                1L,
                LocalDateTime.now()
        );
    }

    // --- TESTES DE LEITURA (GET) ---

    @Test
    @DisplayName("GET /pagamentos - Deve listar todos os pagamentos")
    void deveListarTodos() throws Exception {
        // Busca lista de Pagamentos
        Mockito.when(pagamentoRepository.findAll()).thenReturn(List.of(pagamentoPadrao));

        // Verifica se todos os pagamnetos foram buscados corretamente
        mockMvc.perform(get("/pagamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].valor").value(150.00))
                .andExpect(jsonPath("$[0].idCampanha").value(10));
    }

    @Test
    @DisplayName("GET /pagamentos/{id} - Deve buscar pagamento por ID com sucesso")
    void deveBuscarPorId() throws Exception {
        // Busca Pagamento específico
        Mockito.when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamentoPadrao));

        // Verifica se o pagamento foi retornado corretamente
        mockMvc.perform(get("/pagamentos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.valor").value(150.00));
    }

    @Test
    @DisplayName("GET /pagamentos/{id} - Deve retornar 404 se não encontrar")
    void deveRetornar404AoBuscarInexistente() throws Exception {
        // Busca um pagamento com id inexistente
        Mockito.when(pagamentoRepository.findById(999L)).thenReturn(Optional.empty());

        // Verifica o status de erro
        mockMvc.perform(get("/pagamentos/999"))
                .andExpect(status().isNotFound());
    }

    // --- TESTES DE CRIAÇÃO (POST) ---

    @Test
    @DisplayName("POST /pagamentos - Deve criar pagamento com sucesso")
    void deveCriarPagamento() throws Exception {
        // Cria Pagamento
        Pagamento novoPagamento = new Pagamento();
        novoPagamento.setValor(new BigDecimal("200.00"));
        novoPagamento.setIdCampanha(5L);

        Mockito.when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(invocation -> {
            Pagamento p = invocation.getArgument(0);
            p.setId(2L); // Simula ID gerado
            return p;
        });

        // Verifica se o pagamento foi criado
        mockMvc.perform(post("/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoPagamento)))
                .andExpect(status().isCreated()) // Espera 201 Created
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.valor").value(200.00));
    }

    // --- TESTES DE ATUALIZAÇÃO (PUT) ---

    @Test
    @DisplayName("PUT /pagamentos/{id} - Deve atualizar pagamento com sucesso")
    void deveAtualizarPagamento() throws Exception {
        // Dados para atualização
        Pagamento atualizacao = new Pagamento();
        atualizacao.setValor(new BigDecimal("300.00"));
        atualizacao.setIdCampanha(10L);

        // Resposta esperada
        Pagamento pagamentoEditado = new Pagamento(1L, new BigDecimal("300.00"), 10L, 1L, LocalDateTime.now());

        Mockito.doNothing().when(pagamentoRepository).update(eq(1L), any(Pagamento.class));
        Mockito.when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamentoEditado));

        // Verifica se a alteração ocorreu corretamente
        mockMvc.perform(put("/pagamentos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.valor").value(300.00));
    }

    @Test
    @DisplayName("PUT /pagamentos/{id} - Deve retornar 404 ao tentar atualizar inexistente")
    void deveFalharAoAtualizarInexistente() throws Exception {

        // Pagamento de teste
        Pagamento atualizacao = new Pagamento();
        atualizacao.setValor(new BigDecimal("300.00"));

        Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Pagamento não encontrado"))
                .when(pagamentoRepository).update(eq(999L), any(Pagamento.class));

        // Verifica o status de erro
        mockMvc.perform(put("/pagamentos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizacao)))
                .andExpect(status().isNotFound());
    }

    // --- TESTES DE DELEÇÃO (DELETE) ---

    @Test
    @DisplayName("DELETE /pagamentos/{id} - Deve remover pagamento com sucesso")
    void deveRemoverPagamento() throws Exception {
        // Deleta um Pagamento
        Mockito.doNothing().when(pagamentoRepository).deleteById(1L);

        // Verifica se o pagamento foi deletado corretamente
        mockMvc.perform(delete("/pagamentos/1"))
                .andExpect(status().isNoContent()); // Espera 204 No Content
    }

    @Test
    @DisplayName("DELETE /pagamentos/{id} - Deve retornar 404 ao remover inexistente")
    void deveFalharAoRemoverInexistente() throws Exception {
        // Simula erro 404 vindo do Repositório/Service
        Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Pagamento não encontrado"))
                .when(pagamentoRepository).deleteById(999L);

        // Verifica o status de erro
        mockMvc.perform(delete("/pagamentos/999"))
                .andExpect(status().isNotFound());
    }
}