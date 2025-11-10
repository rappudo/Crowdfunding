package com.eseg.campanhas.service;

import com.eseg.campanhas.model.Campanha;
import com.eseg.campanhas.repository.CampanhaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampanhaService {
    private final CampanhaRepository campanhaRepository;

    public CampanhaService(CampanhaRepository campanhaRepository) {
        this.campanhaRepository = campanhaRepository;
    }

    // 1. Listar todas as campanhas
    public List<Campanha> listarTodas() {
        return campanhaRepository.findAll();
    }

    // 2. Buscar campanha por Id
    public Campanha buscarPorId(Long id) {
        return campanhaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campanha não encontrada"));
    }

    // 3. Criar campanha
    public Campanha criarCampanha(Campanha novaCampanha) {
        return campanhaRepository.save(novaCampanha);
    }

    // 4. Editar campanha
    public Campanha editarCampanha(Long id, Campanha campanhaAtualizada) {
        campanhaRepository.update(id, campanhaAtualizada);
        return buscarPorId(id);
    }

    // 5. Deletar campanha
    public void deletarPorId(Long id) {
        campanhaRepository.deleteById(id);
    }
}
