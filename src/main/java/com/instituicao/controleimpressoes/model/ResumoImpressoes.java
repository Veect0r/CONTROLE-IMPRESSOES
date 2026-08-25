package com.instituicao.controleimpressoes.model;

import java.time.LocalDate;

public class ResumoImpressoes {

    private int totalPB;
    private int totalColorida;
    private int totalGeral;
    private long diasTrabalhados;
    private LocalDate hoje;
    private String nomeMes;

    public ResumoImpressoes() {
    }

    public ResumoImpressoes(int totalPB, int totalColorida, int totalGeral,
            long diasTrabalhados, LocalDate hoje, String nomeMes) {
        this.totalPB = totalPB;
        this.totalColorida = totalColorida;
        this.totalGeral = totalGeral;
        this.diasTrabalhados = diasTrabalhados;
        this.hoje = hoje;
        this.nomeMes = nomeMes;
    }

    public int getTotalPB() {
        return totalPB;
    }

    public int getTotalColorida() {
        return totalColorida;
    }

    public int getTotalGeral() {
        return totalGeral;
    }

    public long getDiasTrabalhados() {
        return diasTrabalhados;
    }

    public LocalDate getHoje() {
        return hoje;
    }

    public String getNomeMes() {
        return nomeMes;
    }
}
