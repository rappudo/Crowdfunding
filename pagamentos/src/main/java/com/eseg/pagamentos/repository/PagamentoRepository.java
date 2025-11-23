package com.eseg.pagamentos.repository;

import com.eseg.pagamentos.model.Pagamento;
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
public class PagamentoRepository {
    @Value("${pagamento.json.path}")
    private String jsonPath;

    private final AtomicLong idGenerator = new AtomicLong();

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @PostConstruct
    public void init() {
        initializeIdGenerator();
    }

    private void initializeIdGenerator() {
        List<Pagamento> pagamentos = loadAll();
        long maxId = pagamentos.stream().mapToLong(Pagamento::getId).max().orElse(0L);
        idGenerator.set(maxId + 1);
    }

    private List<Pagamento> loadAll() {
        try {
            File file = new File(jsonPath);
            if (!file.exists() || file.length() == 0) return new ArrayList<>();
            List<Pagamento> pagamentos = mapper.readValue(file, new TypeReference<List<Pagamento>>(){});
            System.out.println("DEBUG: JSON lido com " + pagamentos.size() + " pagamentos");
            return pagamentos;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveAll(List<Pagamento> pagamentos) {
        try {
            mapper.writeValue(new File(jsonPath), pagamentos);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar pagamento", e);
        }
    }

    // 1. Listar todos os pagamentos
    public List<Pagamento> findAll() {
        return loadAll();
    }

    // 2. Buscar por Id
    public Optional<Pagamento> findById(Long id) {
        return loadAll().stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    // 3. Adcionar novo pagamento
    public Pagamento save(Pagamento novoPagamento) {
        List<Pagamento> pagamentos = loadAll();
        Long novoId = idGenerator.getAndIncrement();
        novoPagamento.setId(novoId);
        pagamentos.add(novoPagamento);
        saveAll(pagamentos);
        return novoPagamento;
    }

    // 4. Atualizar pagamento por Id
    public void update(Long id, Pagamento pagamentoAtualizado) {
        List<Pagamento> pagamentos = loadAll();
        boolean updated = false;
        for (int i = 0; i < pagamentos.size(); i++) {
            if (pagamentos.get(i).getId().equals(id)) {
                pagamentos.set(i, pagamentoAtualizado);
                updated = true;
                break;
            }
        }
        if (!updated) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Campanha não encontrada");
        }
        saveAll(pagamentos);
    }

    // 5. Remover pagamento por ID
    public void deleteById(Long id) {
        List<Pagamento> pagamentos = loadAll();
        boolean removed = pagamentos.removeIf(p -> p.getId().equals(id));
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Campanha não encontrada");
        }
        saveAll(pagamentos);
    }
}
