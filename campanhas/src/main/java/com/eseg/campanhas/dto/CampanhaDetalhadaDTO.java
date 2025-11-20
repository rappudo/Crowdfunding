package com.eseg.campanhas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// DTO detalhado para resposta expandida (/campanhas/{id})
public class CampanhaDetalhadaDTO {
    private Long id;
    private Long idCriador;
    private String titulo;
    private String descricao;
    private BigDecimal meta;
    private BigDecimal valorArrecadado;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataEncerramento;
    private int status; // 0 - encerrada | 1 - em progresso | 2 - cancelada

    // Listas de objetos
    private List<ComentarioDTO> comentarios;
    private List<PagamentoDTO> pagamentos;
    private List<RecompensaDTO> recompensas;
    private UsuarioDTO criador;

    public CampanhaDetalhadaDTO() {}

    public CampanhaDetalhadaDTO(Long id, Long idCriador, String titulo, String descricao, BigDecimal meta, BigDecimal valorArrecadado,
                                LocalDateTime dataCriacao, LocalDateTime dataEncerramento, int status,
                                List<ComentarioDTO> comentarios, List<PagamentoDTO> pagamentos,
                                List<RecompensaDTO> recompensas, UsuarioDTO criador) {
        this.id = id;
        this.idCriador = idCriador;
        this.titulo = titulo;
        this.descricao = descricao;
        this.meta = meta;
        this.valorArrecadado = valorArrecadado;
        this.dataCriacao = dataCriacao;
        this.dataEncerramento = dataEncerramento;
        this.status = status;
        this.comentarios = comentarios;
        this.pagamentos = pagamentos;
        this.recompensas = recompensas;
        this.criador = criador;
    }

    // Getters e setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getMeta() {
        return meta;
    }

    public void setMeta(BigDecimal meta) {
        this.meta = meta;
    }

    public BigDecimal getValorArrecadado() {
        return valorArrecadado;
    }

    public void setValorArrecadado(BigDecimal valorArrecadado) {
        this.valorArrecadado = valorArrecadado;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataEncerramento() {
        return dataEncerramento;
    }

    public void setDataEncerramento(LocalDateTime dataEncerramento) {
        this.dataEncerramento = dataEncerramento;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public UsuarioDTO getCriador() {
        return criador;
    }

    public void setCriador(UsuarioDTO criador) {
        this.criador = criador;
    }
}