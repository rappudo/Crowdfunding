package com.eseg.comentarios;

import com.eseg.comentarios.model.Comentario;
import com.eseg.comentarios.repository.ComentarioRepository;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ComentariosApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Substituímos o repositório real por um Mock (Simulação)
    @MockitoBean
    private ComentarioRepository comentarioRepository;

    // Mockamos o RestTemplate (caso você expanda a lógica no futuro)
    @MockitoBean
    private RestTemplate restTemplate;

    private Comentario comentarioPadrao;

    @BeforeEach
    void setUp() {
        // Objeto padrão para ser usado nos testes
        comentarioPadrao = new Comentario(
                1L,
                "Texto padrão de teste",
                LocalDateTime.now(),
                50L, // idCampanha
                100L // idUsuario
        );
    }

    @Test
    @DisplayName("POST /comentarios - Deve criar um comentário com sucesso")
    void deveCriarComentario() throws Exception {
        // Criação de um comentario
        Comentario novoComentario = new Comentario();
        novoComentario.setTexto("Testando criação Mockada");
        novoComentario.setIdUsuario(100L);
        novoComentario.setIdCampanha(50L);

        Mockito.when(comentarioRepository.save(any(Comentario.class))).thenAnswer(invocation -> {
            Comentario c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        // Verifica se o comentário foi criado corretamente
        mockMvc.perform(post("/comentarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoComentario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.texto").value("Testando criação Mockada"));
    }

    @Test
    @DisplayName("GET /comentarios/{id} - Deve buscar comentário por ID")
    void deveConsultarComentario() throws Exception {
        // Busca comentário por id
        Mockito.when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentarioPadrao));

        // Verifica se a resposta esperada está correta
        mockMvc.perform(get("/comentarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.texto").value("Texto padrão de teste"))
                .andExpect(jsonPath("$.idUsuario").value(100));
    }

    @ParameterizedTest(name = "Teste {index}: Criando comentário \"{0}\"")
    @CsvSource({
            "Adorei o projeto!, 10, 1",
            "Vou doar 100 reais agora., 20, 2",
            "#Sucesso @Time, 99, 5"
    })
    void deveCriarComentariosComDadosVariados(String textoEntrada, Long idUsuarioEntrada, Long idCampanhaEntrada) throws Exception {
        // Comentarios com valores parametrizados
        Comentario input = new Comentario();
        input.setTexto(textoEntrada);
        input.setIdUsuario(idUsuarioEntrada);
        input.setIdCampanha(idCampanhaEntrada);

        Mockito.when(comentarioRepository.save(any(Comentario.class))).thenAnswer(invocation -> {
            Comentario c = invocation.getArgument(0);
            c.setId(99L);
            return c;
        });

        // Verifica se a criação de Comentários ocorreu correntamente
        mockMvc.perform(post("/comentarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.texto").value(textoEntrada))
                .andExpect(jsonPath("$.idUsuario").value(idUsuarioEntrada));
    }

    @Test
    @DisplayName("PUT /comentarios/{id} - Deve atualizar comentário")
    void deveAtualizarComentario() throws Exception {
        // Dados da atualização
        Comentario atualizado = new Comentario();
        atualizado.setTexto("Texto Editado com Sucesso");

        // Objeto que o banco "retornaria" após a edição (para o método buscarPorId que o service chama)
        Comentario comentarioPosEdicao = new Comentario(1L, "Texto Editado com Sucesso", LocalDateTime.now(), 50L, 100L);

        Mockito.doNothing().when(comentarioRepository).update(eq(1L), any(Comentario.class));

        // Busca o Comentário logo após atualizar
        Mockito.when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentarioPosEdicao));

        // Verifica se a alteração ocorreu corretamente
        mockMvc.perform(put("/comentarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.texto").value("Texto Editado com Sucesso"));
    }

    @Test
    @DisplayName("DELETE /comentarios/{id} - Deve deletar comentário")
    void deveDeletarComentario() throws Exception {
        Mockito.doNothing().when(comentarioRepository).deleteById(1L);

        mockMvc.perform(delete("/comentarios/1"))
                .andExpect(status().isNoContent()); // Espera 204
    }

    @Test
    @DisplayName("GET /comentarios/{id} - Deve retornar 404 se não existir")
    void deveRetornar404SeNaoEncontrar() throws Exception {
        // Simulamos que o repositório não encontrou nada (Optional vazio)
        Mockito.when(comentarioRepository.findById(999L)).thenReturn(Optional.empty());

        // Verifica o status de erro
        mockMvc.perform(get("/comentarios/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /comentarios/{id} - Deve retornar 404 ao deletar inexistente")
    void deveRetornar404AoDeletarInexistente() throws Exception {
        // Simulamos o erro que configuramos no Repository
        Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
                .when(comentarioRepository).deleteById(999L);

        // Verifica o status de erro
        mockMvc.perform(delete("/comentarios/999"))
                .andExpect(status().isNotFound());
    }
}