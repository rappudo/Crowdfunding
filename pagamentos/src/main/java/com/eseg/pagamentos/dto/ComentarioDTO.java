package com.eseg.pagamentos.dto;

import java.time.LocalDateTime;

public class ComentarioDTO {
    private long id;
    private String texto;
    private LocalDateTime dataPostagem;
    private long idCampanha;
    private long idUsuario;

    public ComentarioDTO() {}

    public ComentarioDTO(long id, String texto, LocalDateTime dataPostagem, long idCampanha, long idUsuario) {
        this.id = id;
        this.texto = texto;
        this.dataPostagem = dataPostagem;
        this.idCampanha = idCampanha;
        this.idUsuario = idUsuario;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public LocalDateTime getDataPostagem() {
        return dataPostagem;
    }

    public void setDataPostagem(LocalDateTime dataPostagem) {
        this.dataPostagem = dataPostagem;
    }

    public long getIdCampanha() {
        return idCampanha;
    }

    public void setIdCampanha(long idCampanha) {
        this.idCampanha = idCampanha;
    }

    public long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(long idUsuario) {
        this.idUsuario = idUsuario;
    }
}
