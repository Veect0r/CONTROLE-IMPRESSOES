package com.instituicao.controleimpressoes.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RegistroImpressaoRequest {

    @NotNull(message = "Selecione um professor")
    private Integer professorId;

    @NotNull(message = "Informe a quantidade de cópias em preto e branco")
    @Min(value = 0, message = "A quantidade não pode ser negativa")
    private Integer quantidadePB = 0;

    @NotNull(message = "Informe a quantidade de cópias coloridas")
    @Min(value = 0, message = "A quantidade não pode ser negativa")
    private Integer quantidadeColor = 0;

    public Integer getProfessorId() {
        return professorId;
    }

    public void setProfessorId(Integer professorId) {
        this.professorId = professorId;
    }

    public Integer getQuantidadePB() {
        return quantidadePB;
    }

    public void setQuantidadePB(Integer quantidadePB) {
        this.quantidadePB = quantidadePB;
    }

    public Integer getQuantidadeColor() {
        return quantidadeColor;
    }

    public void setQuantidadeColor(Integer quantidadeColor) {
        this.quantidadeColor = quantidadeColor;
    }
}
