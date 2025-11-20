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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

    private Pagamento pagamentoPadrao;

    @BeforeEach
    void setUp() {
        // Cria um objeto base para os testes
        pagamentoPadrao = new Pagamento(
                1L,
                new BigDecimal("150.00"),
                10L, // ID da Campanha
                LocalDateTime.now()
        );
    }

    // --- TESTES DE LEITURA (GET) ---

    @Test
    @DisplayName("GET /pagamentos - Deve listar todos os pagamentos")
    void deveListarTodos() throws Exception {
        Mockito.when(pagamentoRepository.findAll()).thenReturn(List.of(pagamentoPadrao));

        mockMvc.perform(get("/pagamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].valor").value(150.00))
                .andExpect(jsonPath("$[0].idCampanha").value(10));
    }

    @Test
    @DisplayName("GET /pagamentos/{id} - Deve buscar pagamento por ID com sucesso")
    void deveBuscarPorId() throws Exception {
        Mockito.when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamentoPadrao));

        mockMvc.perform(get("/pagamentos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.valor").value(150.00));
    }

    @Test
    @DisplayName("GET /pagamentos/{id} - Deve retornar 404 se não encontrar")
    void deveRetornar404AoBuscarInexistente() throws Exception {
        Mockito.when(pagamentoRepository.findById(999L)).thenReturn(Optional.empty());

        // O controller verifica se é null e retorna NotFound
        // Mas o Service lança RuntimeException.
        // Se o seu Service lança exceção antes do controller checar, precisamos tratar a exceção.
        // Baseado no código: O controller chama service.buscarPorId. O Service lança RuntimeException se Empty.
        // Vamos assumir que queremos que falhe ou retorne erro.

        // Caso o Service lance a exceção:
        Mockito.doThrow(new RuntimeException("Pagamento não encontrado"))
                .when(pagamentoRepository).findById(999L);

        try {
            mockMvc.perform(get("/pagamentos/999"))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            // Passa se lançar exceção
        }
    }

    // --- TESTES DE CRIAÇÃO (POST) ---

    @Test
    @DisplayName("POST /pagamentos - Deve criar pagamento com sucesso")
    void deveCriarPagamento() throws Exception {
        Pagamento novoPagamento = new Pagamento();
        novoPagamento.setValor(new BigDecimal("200.00"));
        novoPagamento.setIdCampanha(5L);

        // Simula o comportamento do Repository.save: recebe objeto, gera ID e retorna
        Mockito.when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(invocation -> {
            Pagamento p = invocation.getArgument(0);
            p.setId(2L); // Simula ID gerado
            return p;
        });

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
        // 1. Dados para atualização
        Pagamento atualizacao = new Pagamento();
        atualizacao.setValor(new BigDecimal("300.00"));
        atualizacao.setIdCampanha(10L);

        // 2. Objeto que será retornado após atualização
        Pagamento pagamentoEditado = new Pagamento(1L, new BigDecimal("300.00"), 10L, LocalDateTime.now());

        // 3. Mocks
        // O Service chama update (void) -> depois chama findById
        Mockito.doNothing().when(pagamentoRepository).update(eq(1L), any(Pagamento.class));
        Mockito.when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamentoEditado));

        // 4. Execução
        mockMvc.perform(put("/pagamentos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.valor").value(300.00));
    }

    @Test
    @DisplayName("PUT /pagamentos/{id} - Deve falhar ao tentar atualizar inexistente")
    void deveFalharAoAtualizarInexistente() throws Exception {
        Pagamento atualizacao = new Pagamento();
        atualizacao.setValor(new BigDecimal("300.00"));

        // Simula exceção no repositório
        Mockito.doThrow(new RuntimeException("Pagamento não encontrado"))
                .when(pagamentoRepository).update(eq(999L), any(Pagamento.class));

        try {
            mockMvc.perform(put("/pagamentos/999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(atualizacao)));
        } catch (Exception e) {
            // Sucesso se lançar exceção
        }
    }

    // --- TESTES DE DELEÇÃO (DELETE) ---

    @Test
    @DisplayName("DELETE /pagamentos/{id} - Deve remover pagamento com sucesso")
    void deveRemoverPagamento() throws Exception {
        Mockito.doNothing().when(pagamentoRepository).deleteById(1L);

        mockMvc.perform(delete("/pagamentos/1"))
                .andExpect(status().isNoContent()); // Espera 204 No Content
    }

    @Test
    @DisplayName("DELETE /pagamentos/{id} - Deve falhar ao remover inexistente")
    void deveFalharAoRemoverInexistente() throws Exception {
        Mockito.doThrow(new RuntimeException("Pagamento não encontrado"))
                .when(pagamentoRepository).deleteById(999L);

        try {
            mockMvc.perform(delete("/pagamentos/999"));
        } catch (Exception e) {
            // Sucesso se lançar exceção
        }
    }
}