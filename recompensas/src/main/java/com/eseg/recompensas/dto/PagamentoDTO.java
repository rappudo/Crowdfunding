package com.eseg.recompensas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagamentoDTO {
    private Long id;
    private BigDecimal valor;
    private Long idCampanha;
    private LocalDateTime dataPagamento;

    public PagamentoDTO() {}

    public PagamentoDTO(Long id, BigDecimal valor, Long idCampanha, LocalDateTime dataPagamento) {
        this.id = id;
        this.valor = valor;
        this.idCampanha = idCampanha;
        this.dataPagamento = dataPagamento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Long getIdCampanha() {
        return idCampanha;
    }

    public void setIdCampanha(Long idCampanha) {
        this.idCampanha = idCampanha;
    }

    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDateTime dataPagamento) {
        this.dataPagamento = dataPagamento;
    }
}
