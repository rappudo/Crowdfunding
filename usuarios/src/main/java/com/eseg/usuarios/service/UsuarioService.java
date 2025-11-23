package com.eseg.usuarios.service;

import com.eseg.usuarios.model.Usuario;
import com.eseg.usuarios.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // 1. Listar todos os usuários
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    // 2. Buscar usuário por Id
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campanha não encontrada"));
    }

    // 3. Criar usuário
    public Usuario criarUsuario(Usuario novoUsuario) {
        return usuarioRepository.save(novoUsuario);
    }

    // 4. Editar usuário
    public Usuario editarUsuario(Long id, Usuario usuarioAtualizado) {
        usuarioRepository.update(id, usuarioAtualizado);
        return buscarPorId(id);
    }

    // 5. Deletar usuário
    public void deletarPorId(Long id) {
        usuarioRepository.deleteById(id);
    }
}
