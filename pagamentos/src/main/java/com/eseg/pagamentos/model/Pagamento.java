package com.eseg.pagamentos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pagamento {
    private long id;
    private BigDecimal valor;
    private long idCampanha;
    private LocalDateTime dataPagamento;

    public Pagamento() {}

    public Pagamento(long id, BigDecimal valor, long idCampanha, LocalDateTime dataPagamento) {
        this.id = id;
        this.valor = valor;
        this.idCampanha = idCampanha;
        this.dataPagamento = dataPagamento;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public long getIdCampanha() {
        return idCampanha;
    }

    public void setIdCampanha(long idCampanha) {
        this.idCampanha = idCampanha;
    }

    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDateTime dataPagamento) {
        this.dataPagamento = dataPagamento;
    }
}
