package com.eseg.usuarios.repository;

import com.eseg.usuarios.model.Usuario;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UsuarioRepository {
    @Value("${usuario.json.path}")
    private String jsonPath;

    private final AtomicLong idGenerator = new AtomicLong();

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @PostConstruct
    public void init() {
        initializeIdGenerator();
    }

    private void initializeIdGenerator() {
        List<Usuario> usuarios = loadAll();
        long maxId = usuarios.stream().mapToLong(Usuario::getId).max().orElse(0L);
        idGenerator.set(maxId + 1);
    }

    private List<Usuario> loadAll() {
        try {
            File file = new File(jsonPath);
            if (!file.exists() || file.length() == 0) return new ArrayList<>();
            List<Usuario> usuarios = mapper.readValue(file, new TypeReference<List<Usuario>>(){});
            System.out.println("DEBUG: JSON lido com " + usuarios.size() + " usuarios");
            return usuarios;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveAll(List<Usuario> usuarios) {
        try {
            mapper.writeValue(new File(jsonPath), usuarios);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar usuário", e);
        }
    }

    // 1. Listar todos os usuarios
    public List<Usuario> findAll() {
        return loadAll();
    }

    // 2. Buscar por Id
    public Optional<Usuario> findById(Long id) {
        return loadAll().stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    // 3. Adcionar novo usuario
    public Usuario save(Usuario novoUsuario) {
        List<Usuario> usuarios = loadAll();
        Long novoId = idGenerator.getAndIncrement();
        novoUsuario.setId(novoId);
        usuarios.add(novoUsuario);
        saveAll(usuarios);
        return novoUsuario;
    }

    // 4. Atualizar usuario por Id
    public void update(Long id, Usuario usuarioAtualizado) {
        List<Usuario> usuarios = loadAll();
        boolean updated = false;
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getId().equals(id)) {
                usuarios.set(i, usuarioAtualizado);
                updated = true;
                break;
            }
        }
        if (!updated) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Campanha não encontrada");
        }
        saveAll(usuarios);
    }

    // 5. Remover usuário por ID
    public void deleteById(Long id) {
        List<Usuario> usuarios = loadAll();
        boolean removed = usuarios.removeIf(p -> p.getId().equals(id));
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Campanha não encontrada");
        }
        saveAll(usuarios);
    }
}
