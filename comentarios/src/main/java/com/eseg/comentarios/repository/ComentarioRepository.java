package com.eseg.comentarios.repository;

import com.eseg.comentarios.model.Comentario;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ComentarioRepository {
    @Value("${comentario.json.path}")
    private String jsonPath;

    private final AtomicLong idGenerator = new AtomicLong();

    private final ObjectMapper mapper = new ObjectMapper();

    public ComentarioRepository() {
        initializeIdGenerator();
    }

    private void initializeIdGenerator() {
        List<Comentario> comentarios = loadAll();
        long maxId = comentarios.stream().mapToLong(Comentario::getId).max().orElse(0L);
        idGenerator.set(maxId + 1);
    }

    private List<Comentario> loadAll() {
        try {
            File file = new File(jsonPath);
            if (!file.exists() || file.length() == 0) return new ArrayList<>();
            return mapper.readValue(file, new TypeReference<List<Comentario>>() {});
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private void saveAll(List<Comentario> comentarios) {
        try {
            mapper.writeValue(new File(jsonPath), comentarios);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar comentário", e);
        }
    }

    // 1. Listar todos os comentarios
    public List<Comentario> findAll() {
        return loadAll();
    }

    // 2. Buscar por Id
    public Optional<Comentario> findById(Long id) {
        return loadAll().stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    // 3. Adcionar novo comentário
    public Comentario save(Comentario novoComentario) {
        List<Comentario> comentarios = loadAll();
        Long novoId = idGenerator.getAndIncrement();
        novoComentario.setId(novoId);
        comentarios.add(novoComentario);
        saveAll(comentarios);
        return novoComentario;
    }

    // 4. Atualizar comentário por Id
    public void update(Long id, Comentario comentarioAtualizado) {
        List<Comentario> comentarios = loadAll();
        boolean updated = false;
        for (int i = 0; i < comentarios.size(); i++) {
            if (comentarios.get(i).getId().equals(id)) {
                comentarios.set(i, comentarioAtualizado);
                updated = true;
                break;
            }
        }
        if (!updated) {
            throw new RuntimeException("Comentario com id " + id + " não encontrado.");
        }
        saveAll(comentarios);
    }

    // 5. Remover comentário por ID
    public void deleteById(Long id) {
        List<Comentario> comentarios = loadAll();
        boolean removed = comentarios.removeIf(c -> c.getId().equals(id));
        if (!removed) {
            throw new RuntimeException("Comentário com id " + id + " não encontrado.");
        }
        saveAll(comentarios);
    }


}
