package com.eseg.campanhas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class CampanhaDTO {
    private Long id;
    private String titulo;
    private String descricao;
    private BigDecimal meta;
    private BigDecimal valorArrecadado;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataEncerramento;
    private int status; // 0 - encerrada | 1 - em progresso | 2 - cancelada

    private List<Long> idPagamentos;
    private List<Long> idComentarios;

    public CampanhaDTO() {}

    public CampanhaDTO(Long id, String titulo, String descricao, BigDecimal meta,
                    BigDecimal valorArrecadado, LocalDateTime dataCriacao, LocalDateTime dataEncerramento, int status) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.meta = meta;
        this.valorArrecadado = valorArrecadado;
        this.dataCriacao = dataCriacao;
        this.dataEncerramento = dataEncerramento;
        this.status = status;
        this.idPagamentos = new ArrayList<>();
        this.idComentarios = new ArrayList<>();
    }

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

    public List<Long> getIdPagamentos() {
        return idPagamentos;
    }

    public void setIdPagamentos(List<Long> idPagamentos) {
        this.idPagamentos = idPagamentos;
    }

    public List<Long> getIdComentarios() {
        return idComentarios;
    }

    public void setIdComentarios(List<Long> idComentarios) {
        this.idComentarios = idComentarios;
    }
}
