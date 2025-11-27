package com.eseg.campanhas.repository;

import com.eseg.campanhas.model.Campanha;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        // Garante que o diretório existe
        try {
            Path path = Paths.get(jsonPath);
            Path parentDir = path.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
                System.out.println("Diretório criado: " + parentDir);
            }
            
            // Cria arquivo vazio se não existir
            if (!Files.exists(path)) {
                mapper.writeValue(path.toFile(), new ArrayList<Campanha>());
                System.out.println("Arquivo JSON criado: " + jsonPath);
            }
        } catch (IOException e) {
            System.err.println("Erro ao inicializar arquivo JSON: " + e.getMessage());
        }
        
        initializeIdGenerator();
    }

    private void initializeIdGenerator() {
        List<Campanha> campanhas = loadAll();
        long maxId = campanhas.stream().mapToLong(Campanha::getId).max().orElse(0L);
        idGenerator.set(maxId + 1);
        System.out.println("ID Generator iniciado em: " + idGenerator.get());
    }

    private List<Campanha> loadAll() {
        try {
            File file = new File(jsonPath);
            
            if (!file.exists()) {
                System.out.println("Arquivo não existe, retornando lista vazia");
                return new ArrayList<>();
            }
            
            if (file.length() == 0) {
                System.out.println("Arquivo vazio, retornando lista vazia");
                return new ArrayList<>();
            }
            
            List<Campanha> campanhas = mapper.readValue(file, new TypeReference<List<Campanha>>(){});
            System.out.println("JSON lido com " + campanhas.size() + " campanhas");
            return campanhas;
            
        } catch (IOException e) {
            System.err.println("Erro ao ler JSON: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveAll(List<Campanha> campanhas) {
        try {
            File file = new File(jsonPath);
            
            // Garante que o diretório pai existe
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
                System.out.println("Diretório criado: " + parentDir.getAbsolutePath());
            }
            
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, campanhas);
            System.out.println("JSON salvo com " + campanhas.size() + " campanhas em: " + file.getAbsolutePath());
            
        } catch (IOException e) {
            System.err.println("Erro ao salvar JSON: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar campanhas: " + e.getMessage(), e);
        }
    }

    // 1. Listar todas as campanhas
    public List<Campanha> findAll() {
        return loadAll();
    }

    // 2. Buscar por Id
    public Optional<Campanha> findById(Long id) {
    return loadAll().stream()
        .filter(c -> c.getId() != null && c.getId().equals(id))  
        .findFirst();
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
        if (campanhas.get(i).getId() != null && campanhas.get(i).getId().equals(id)) {
            campanhaAtualizada.setId(id);
            
            Campanha antiga = campanhas.get(i);
            if (campanhaAtualizada.getIdComentarios() == null) {
                campanhaAtualizada.setIdComentarios(antiga.getIdComentarios() != null ? antiga.getIdComentarios() : new ArrayList<>());
            }
            if (campanhaAtualizada.getIdPagamentos() == null) {
                campanhaAtualizada.setIdPagamentos(antiga.getIdPagamentos() != null ? antiga.getIdPagamentos() : new ArrayList<>());
            }
            if (campanhaAtualizada.getIdRecompensas() == null) {
                campanhaAtualizada.setIdRecompensas(antiga.getIdRecompensas() != null ? antiga.getIdRecompensas() : new ArrayList<>());
            }
            
            campanhas.set(i, campanhaAtualizada);
            updated = true;
            break;
        }
    }
    
    if (!updated) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Campanha com id " + id + " não encontrada.");
    }
    
    saveAll(campanhas);
    System.out.println("✅ Campanha " + id + " atualizada com sucesso!");
}

    // 5. Remover campanha por ID
    public void deleteById(Long id) {
        List<Campanha> campanhas = loadAll();
        boolean removed = campanhas.removeIf(c -> c.getId().equals(id));
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Campanha com id " + id + " não encontrada.");
        }
        saveAll(campanhas);
    }
}
