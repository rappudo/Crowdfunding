package com.eseg.recompensas.repository;

import com.eseg.recompensas.model.Recompensa;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class RecompensaRepository {
    @Value("${recompensa.json.path}")
    private String jsonPath;

    private final AtomicLong idGenerator = new AtomicLong();

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @PostConstruct
    public void init() {
        initializeIdGenerator();
    }

    private void initializeIdGenerator() {
        List<Recompensa> recompensas = loadAll();
        long maxId = recompensas.stream().mapToLong(Recompensa::getId).max().orElse(0L);
        idGenerator.set(maxId + 1);
    }

    private List<Recompensa> loadAll() {
        try {
            File file = new File(jsonPath);
            if (!file.exists() || file.length() == 0) return new ArrayList<>();
            List<Recompensa> recompensas = mapper.readValue(file, new TypeReference<List<Recompensa>>(){});
            System.out.println("DEBUG: JSON lido com " + recompensas.size() + " recompensas");
            return recompensas;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveAll(List<Recompensa> recompensas) {
        try {
            mapper.writeValue(new File(jsonPath), recompensas);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar recompensa", e);
        }
    }

    // 1. Listar todas as recompensas
    public List<Recompensa> findAll() {
        return loadAll();
    }

    // 2. Buscar por Id
    public Optional<Recompensa> findById(Long id) {
        return loadAll().stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    // 3. Adcionar nova recompensa
    public Recompensa save(Recompensa novaRecompensa) {
        List<Recompensa> recompensas = loadAll();
        Long novoId = idGenerator.getAndIncrement();
        novaRecompensa.setId(novoId);
        recompensas.add(novaRecompensa);
        saveAll(recompensas);
        return novaRecompensa;
    }

    // 4. Atualizar recompensa por Id
    public void update(Long id, Recompensa recompensaAtualizada) {
        List<Recompensa> recompensas = loadAll();
        boolean updated = false;
        for (int i = 0; i < recompensas.size(); i++) {
            if (recompensas.get(i).getId().equals(id)) {
                recompensas.set(i, recompensaAtualizada);
                updated = true;
                break;
            }
        }
        if (!updated) {
            throw new RuntimeException("Recompensa com id " + id + " não encontrada.");
        }
        saveAll(recompensas);
    }

    // 5. Remover recompensa por ID
    public void deleteById(Long id) {
        List<Recompensa> recompensas = loadAll();
        boolean removed = recompensas.removeIf(p -> p.getId().equals(id));
        if (!removed) {
            throw new RuntimeException("Recompensa com id " + id + " não encontrada");
        }
        saveAll(recompensas);
    }
}
