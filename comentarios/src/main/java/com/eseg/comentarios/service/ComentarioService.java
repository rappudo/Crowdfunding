package com.eseg.comentarios.service;

import com.eseg.comentarios.model.Comentario;
import com.eseg.comentarios.repository.ComentarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
public class ComentarioService {
    private final ComentarioRepository comentarioRepository;

    public ComentarioService(ComentarioRepository comentarioRepository) {this.comentarioRepository = comentarioRepository;}

    //1. Carregar os comentários de uma campanha
    public List<Comentario> listarTodos() {
            return comentarioRepository.findAll();
    }


    //2. Carregar os comentários por usuário
    public Comentario comentarioPorID(Long id){
        return comentarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campanha não encontrada"));
    }

    //3. Criar novo Comentário
    public Comentario criarComentario(Comentario novoComentario) {
        return comentarioRepository.save(novoComentario);
    }

    // 4. Editar Comentario
    public Comentario editarComentario(Long id, Comentario comentarioAtualizado) {
        comentarioRepository.update(id, comentarioAtualizado);
        return comentarioPorID(id);
    }

    // 5. Deletar usuário
    public void deletarPorId(Long id) {
        comentarioRepository.deleteById(id);
    }

}
