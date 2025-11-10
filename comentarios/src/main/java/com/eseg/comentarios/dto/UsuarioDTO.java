package com.eseg.comentarios.dto;

import java.util.List;
import java.util.ArrayList;

public class UsuarioDTO {
    private Long id;
    private String nome;
    private String telefone;
    private String email;

    private List<Long> idCampanhasCriadas;
    private List<Long> idComentariosFeitos;
    private List<Long> idPagamentosFeitos;
    private List<Long> idRecompensasRecebidas;

    public UsuarioDTO() {}

    public UsuarioDTO(Long id, String nome, String telefone, String email) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.idCampanhasCriadas = new ArrayList<>();
        this.idComentariosFeitos =  new  ArrayList<>();
        this.idPagamentosFeitos = new  ArrayList<>();
        this.idRecompensasRecebidas = new  ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Long> getIdCampanhasCriadas() {
        return idCampanhasCriadas;
    }

    public void setIdCampanhasCriadas(List<Long> idCampanhasCriadas) {
        this.idCampanhasCriadas = idCampanhasCriadas;
    }

    public List<Long> getIdComentariosFeitos() {
        return idComentariosFeitos;
    }

    public void setIdComentariosFeitos(List<Long> idComentariosFeitos) {
        this.idComentariosFeitos = idComentariosFeitos;
    }

    public List<Long> getIdPagamentosFeitos() {
        return idPagamentosFeitos;
    }

    public void setIdPagamentosFeitos(List<Long> idPagamentosFeitos) {
        this.idPagamentosFeitos = idPagamentosFeitos;
    }

    public List<Long> getIdRecompensasRecebidas() {
        return idRecompensasRecebidas;
    }

    public void setIdRecompensasRecebidas(List<Long> idRecompensasRecebidas) {
        this.idRecompensasRecebidas = idRecompensasRecebidas;
    }
}
