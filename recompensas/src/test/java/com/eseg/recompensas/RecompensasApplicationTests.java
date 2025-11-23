package com.eseg.recompensas;

import com.eseg.recompensas.model.Recompensa;
import com.eseg.recompensas.repository.RecompensaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RecompensasApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mockamos o repositório de recompensas
    @MockitoBean
    private RecompensaRepository recompensaRepository;

    private Recompensa recompensaPadrao;

    @BeforeEach
    void setUp() {
        // Recompensa padrão para ser usado nos testes
        recompensaPadrao = new Recompensa(
                1L,
                "Camiseta Exclusiva",
                "Uma camiseta de algodão com a logo do projeto",
                new BigDecimal("50.00"),
                10L // idCampanha
        );
    }

    // --- TESTES DE LEITURA (GET) ---

    @Test
    @DisplayName("GET /recompensas - Deve listar todas as recompensas")
    void deveListarTodas() throws Exception {
        // Busca todas as recompensas
        Mockito.when(recompensaRepository.findAll()).thenReturn(List.of(recompensaPadrao));

        // Verifica se todas as Recompensas foram retornadas
        mockMvc.perform(get("/recompensas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Camiseta Exclusiva"))
                .andExpect(jsonPath("$[0].valorMinimo").value(50.00));
    }

    @Test
    @DisplayName("GET /recompensas/{id} - Deve buscar recompensa por ID")
    void deveBuscarPorId() throws Exception {
        // Busca uma Recompensa específica
        Mockito.when(recompensaRepository.findById(1L)).thenReturn(Optional.of(recompensaPadrao));

        // Verifica se a recompensa foi retornada corretamente
        mockMvc.perform(get("/recompensas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Camiseta Exclusiva"));
    }

    @Test
    @DisplayName("GET /recompensas/{id} - Deve retornar 404 se não existir")
    void deveRetornar404AoBuscarInexistente() throws Exception {

        Mockito.when(recompensaRepository.findById(999L)).thenReturn(Optional.empty());

        // Verifica o status de erro
        mockMvc.perform(get("/recompensas/999"))
                .andExpect(status().isNotFound());
    }

    // --- TESTES DE CRIAÇÃO (POST) ---

    @Test
    @DisplayName("POST /recompensas - Deve criar recompensa com sucesso")
    void deveCriarRecompensa() throws Exception {

        //Recompensa para teste
        Recompensa novaRecompensa = new Recompensa();
        novaRecompensa.setTitulo("Adesivo");
        novaRecompensa.setDescricao("Adesivo brilhante");
        novaRecompensa.setValorMinimo(new BigDecimal("10.00"));
        novaRecompensa.setIdCampanha(5L);

        Mockito.when(recompensaRepository.save(any(Recompensa.class))).thenAnswer(invocation -> {
            Recompensa r = invocation.getArgument(0);
            r.setId(2L);
            return r;
        });

        // Verifica se a Recompensa foi criada corretamente
        mockMvc.perform(post("/recompensas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novaRecompensa)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.titulo").value("Adesivo"));
    }

    // Teste parametrizado similar ao de Comentários
    @ParameterizedTest(name = "Teste {index}: Criando recompensa \"{0}\" com valor {1}")
    @CsvSource({
            "Caneca Mágica, 35.00, 1",
            "Pôster Autografado, 100.00, 2",
            "VIP Access, 500.00, 3"
    })
    void deveCriarRecompensasComDadosVariados(String titulo, String valorString, Long idCampanha) throws Exception {
        //Cria recompensas com valores parametrizados
        Recompensa input = new Recompensa();
        input.setTitulo(titulo);
        input.setValorMinimo(new BigDecimal(valorString));
        input.setIdCampanha(idCampanha);

        Mockito.when(recompensaRepository.save(any(Recompensa.class))).thenAnswer(invocation -> {
            Recompensa r = invocation.getArgument(0);
            r.setId(99L);
            return r;
        });

        // Verifica se todas as Recompensas foram criadas corretamente
        mockMvc.perform(post("/recompensas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value(titulo))
                .andExpect(jsonPath("$.valorMinimo").value(Double.parseDouble(valorString)));
    }

    // --- TESTES DE ATUALIZAÇÃO (PUT) ---

    @Test
    @DisplayName("PUT /recompensas/{id} - Deve atualizar recompensa")
    void deveAtualizarRecompensa() throws Exception {
        // Dados da atualização
        Recompensa atualizacao = new Recompensa();
        atualizacao.setTitulo("Camiseta Editada");
        atualizacao.setValorMinimo(new BigDecimal("60.00"));

        // Recompensa após o update
        Recompensa recompensaPosEdicao = new Recompensa(1L, "Camiseta Editada", "Desc", new BigDecimal("60.00"), 10L);

        Mockito.doNothing().when(recompensaRepository).update(eq(1L), any(Recompensa.class));
        Mockito.when(recompensaRepository.findById(1L)).thenReturn(Optional.of(recompensaPosEdicao));

        //Verifica se a Recompensa foi atualizada
        mockMvc.perform(put("/recompensas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Camiseta Editada"))
                .andExpect(jsonPath("$.valorMinimo").value(60.00));
    }

    @Test
    @DisplayName("PUT /recompensas/{id} - Deve retornar 404 ao atualizar inexistente")
    void deveRetornar404AoAtualizarInexistente() throws Exception {
        //Recompensa de teste
        Recompensa atualizacao = new Recompensa();
        atualizacao.setTitulo("Fantasma");

        Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Campanha não encontrada"))
                .when(recompensaRepository).update(eq(999L), any(Recompensa.class));

        // Verifica o status de erro
        mockMvc.perform(put("/recompensas/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizacao)))
                .andExpect(status().isNotFound());
    }

    // --- TESTES DE DELEÇÃO (DELETE) ---

    @Test
    @DisplayName("DELETE /recompensas/{id} - Deve deletar recompensa")
    void deveDeletarRecompensa() throws Exception {
        // Deleta uma Recompensa
        Mockito.doNothing().when(recompensaRepository).deleteById(1L);

        // Verifica se a Recompensa foi deletada corretamente
        mockMvc.perform(delete("/recompensas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /recompensas/{id} - Deve retornar 404 ao deletar inexistente")
    void deveRetornar404AoDeletarInexistente() throws Exception {
        // Simulamos o erro 404 vindo do Repositório
        Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Campanha não encontrada"))
                .when(recompensaRepository).deleteById(999L);

        // Verifica o status de erro
        mockMvc.perform(delete("/recompensas/999"))
                .andExpect(status().isNotFound());
    }
}