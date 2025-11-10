package com.eseg.comentarios.dto;

import java.math.BigDecimal;

public class RecompensaDTO {
    private Long id;
    private String titulo;
    private String Descricao;
    private BigDecimal valorMinimo;
    private Long idCampanha;

    public RecompensaDTO() {}

    public RecompensaDTO(Long id, String titulo, String descricao, BigDecimal valorMinimo, Long idCampanha) {
        this.id = id;
        this.titulo = titulo;
        Descricao = descricao;
        this.valorMinimo = valorMinimo;
        this.idCampanha = idCampanha;
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
        return Descricao;
    }

    public void setDescricao(String descricao) {
        Descricao = descricao;
    }

    public BigDecimal getValorMinimo() {
        return valorMinimo;
    }

    public void setValorMinimo(BigDecimal valorMinimo) {
        this.valorMinimo = valorMinimo;
    }

    public Long getIdCampanha() {
        return idCampanha;
    }

    public void setIdCampanha(Long idCampanha) {
        this.idCampanha = idCampanha;
    }
}
