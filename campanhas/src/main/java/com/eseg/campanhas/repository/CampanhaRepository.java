package com.eseg.campanhas.repository;

import com.eseg.campanhas.model.Campanha;
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
public class CampanhaRepository {
    @Value("${campanha.json.path}")
    private String jsonPath;

    private final AtomicLong idGenerator = new AtomicLong();

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @PostConstruct
    public void init() {
        initializeIdGenerator();
    }

    private void initializeIdGenerator() {
        List<Campanha> campanhas = loadAll();
        long maxId = campanhas.stream().mapToLong(Campanha::getId).max().orElse(0L);
        idGenerator.set(maxId + 1);
    }

    private List<Campanha> loadAll() {
        try {
            File file = new File(jsonPath);
            if (!file.exists() || file.length() == 0) return new ArrayList<>();
            List<Campanha> campanhas = mapper.readValue(file, new TypeReference<List<Campanha>>(){});
            System.out.println("DEBUG: JSON lido com " + campanhas.size() + " campanhas");
            return campanhas;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveAll(List<Campanha> campanhas) {
        try {
            Resource resource = new ClassPathResource(jsonPath.replace("classpath:", ""));
            File file = resource.getFile();
            mapper.writeValue(file, campanhas);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar campanhas", e);
        }
    }

    // 1. Listar todas as campanhas
    public List<Campanha> findAll() {
        return loadAll();
    }

    // 2. Buscar por Id
    public Optional<Campanha> findById(Long id) {
        return loadAll().stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    // 3. Adicionar nova campanha
    public Campanha save(Campanha novaCampanha) {
        List<Campanha> campanhas = loadAll();
        Long novoId = idGenerator.getAndIncrement();
        novaCampanha.setId(novoId);
        campanhas.add(novaCampanha);
        saveAll(campanhas);
        return novaCampanha;
    }

    // 4. Atualizar campanha por Id
    public void update(Long id, Campanha campanhaAtualizada) {
        List<Campanha> campanhas = loadAll();
        boolean updated = false;
        for (int i = 0; i < campanhas.size(); i++) {
            if (campanhas.get(i).getId().equals(id)) {
                campanhas.set(i, campanhaAtualizada);
                updated = true;
                break;
            }
        }
        if (!updated) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentário com id " + id + " não encontrado.");
        }
        saveAll(campanhas);
    }

    // 5. Remover campanha por ID
    public void deleteById(Long id) {
        List<Campanha> campanhas = loadAll();
        boolean removed = campanhas.removeIf(c -> c.getId().equals(id));
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentário com id " + id + " não encontrado.");
        }
        saveAll(campanhas);
    }
}
