package com.eseg.recompensas.model;

import java.math.BigDecimal;

public class Recompensa {
    private long id;
    private String titulo;
    private String Descricao;
    private BigDecimal valorMinimo;
    private long idCampanha;

    public Recompensa() {}

    public Recompensa(long id, String titulo, String descricao, BigDecimal valorMinimo, long idCampanha) {
        this.id = id;
        this.titulo = titulo;
        Descricao = descricao;
        this.valorMinimo = valorMinimo;
        this.idCampanha = idCampanha;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public long getIdCampanha() {
        return idCampanha;
    }

    public void setIdCampanha(long idCampanha) {
        this.idCampanha = idCampanha;
    }
}
