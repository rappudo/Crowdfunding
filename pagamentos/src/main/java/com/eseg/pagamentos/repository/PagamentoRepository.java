package com.eseg.pagamentos.repository;

import com.eseg.pagamentos.model.Pagamento;
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
public class PagamentoRepository {
    @Value("${pagamento.json.path}")
    private String jsonPath;

    private final AtomicLong idGenerator = new AtomicLong();

    private final ObjectMapper mapper = new ObjectMapper();

    public PagamentoRepository() {
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
            return mapper.readValue(file, new TypeReference<List<Pagamento>>(){});
        } catch (IOException e) {
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

    // 2. Buscar por ID
    public Optional<Pagamento> findByID(Long id) {
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

    // 4. Atualizar pagamento por ID
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
            throw new RuntimeException("Pagamento com id " + id + " não encontrado.");
        }
        saveAll(pagamentos);
    }

    // 5. Remover pagamento por ID
    public void deleteById(Long id) {
        List<Pagamento> pagamentos = loadAll();
        boolean removed = pagamentos.removeIf(p -> p.getId().equals(id));
        if (!removed) {
            throw new RuntimeException("Pagamento com id " + id + " não encontrado");
        }
        saveAll(pagamentos);
    }
}
