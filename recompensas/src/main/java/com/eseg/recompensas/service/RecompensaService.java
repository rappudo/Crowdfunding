package com.eseg.recompensas.service;

import com.eseg.recompensas.model.Recompensa;
import com.eseg.recompensas.repository.RecompensaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RecompensaService {

    private final RecompensaRepository recompensaRepository;

    public RecompensaService(RecompensaRepository recompensaRepository) {
        this.recompensaRepository = recompensaRepository;
    }

    //Listar todas as recompensas
    public List<Recompensa> listarTodos() {
        return recompensaRepository.findAll();
    }

    //Recompensa por ID
    public Recompensa buscaPorId(Long id) {
        return recompensaRepository.findById(id).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campanha não encontrada"));
    }

    //Criar Recompensa
    public Recompensa criarRecompensa(Recompensa novaRecompensa) {
        return recompensaRepository.save(novaRecompensa);
    }

    //Editar Recompensa
    public Recompensa editarRecompensa(Long id, Recompensa recompensaEditada) {
        recompensaRepository.update(id, recompensaEditada);
        return buscaPorId(id);
    }

    //Deletar Recompensa
    public void deletarPorId(Long id) {
        recompensaRepository.deleteById(id);
    }

}
