package com.eseg.pagamentos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pagamento {
    private Long id;
    private BigDecimal valor;
    private Long idCampanha;
    private Long idUsuario;
    private LocalDateTime dataPagamento;

    public Pagamento() {}

    public Pagamento(Long id, BigDecimal valor, Long idCampanha, Long idUsuario, LocalDateTime dataPagamento) {
        this.id = id;
        this.valor = valor;
        this.idCampanha = idCampanha;
        this.idUsuario = idUsuario;
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

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }
}
