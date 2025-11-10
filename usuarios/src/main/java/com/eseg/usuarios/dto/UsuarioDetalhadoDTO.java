package com.eseg.usuarios.dto;

import java.util.List;

// DTO detalhado para resposta expandida (/usuarios/{id})
public class UsuarioDetalhadoDTO {
    private Long id;
    private String nome;
    private String telefone;
    private String email;

    // Listas de objetos
    private List<CampanhaDTO> campanhas;
    private List<ComentarioDTO> comentarios;
    private List<PagamentoDTO> pagamentos;
    private List<RecompensaDTO> recompensas;

    public UsuarioDetalhadoDTO() {
    }

    public UsuarioDetalhadoDTO(Long id, String nome, String telefone, String email,
                               List<CampanhaDTO> campanhas, List<ComentarioDTO> comentarios,
                               List<PagamentoDTO> pagamentos, List<RecompensaDTO> recompensas) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.campanhas = campanhas;
        this.comentarios = comentarios;
        this.pagamentos = pagamentos;
        this.recompensas = recompensas;
    }

    // Getters e setters

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

    public List<CampanhaDTO> getCampanhas() {
        return campanhas;
    }

    public void setCampanhas(List<CampanhaDTO> campanhas) {
        this.campanhas = campanhas;
    }

    public List<ComentarioDTO> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<ComentarioDTO> comentarios) {
        this.comentarios = comentarios;
    }

    public List<PagamentoDTO> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<PagamentoDTO> pagamentos) {
        this.pagamentos = pagamentos;
    }

    public List<RecompensaDTO> getRecompensas() {
        return recompensas;
    }

    public void setRecompensas(List<RecompensaDTO> recompensas) {
        this.recompensas = recompensas;
    }
}