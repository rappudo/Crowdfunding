package com.eseg.pagamentos.dto;

import java.time.LocalDateTime;

public class ComentarioDTO {
    private Long id;
    private String texto;
    private LocalDateTime dataPostagem;
    private Long idCampanha;
    private Long idUsuario;

    public ComentarioDTO() {}

    public ComentarioDTO(Long id, String texto, LocalDateTime dataPostagem, Long idCampanha, Long idUsuario) {
        this.id = id;
        this.texto = texto;
        this.dataPostagem = dataPostagem;
        this.idCampanha = idCampanha;
        this.idUsuario = idUsuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getIdCampanha() {
        return idCampanha;
    }

    public void setIdCampanha(Long idCampanha) {
        this.idCampanha = idCampanha;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }
}
