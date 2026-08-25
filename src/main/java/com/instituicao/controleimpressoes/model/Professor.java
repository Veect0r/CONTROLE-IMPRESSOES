package com.instituicao.controleimpressoes.model;

public class Professor {

    private Integer id;
    private String nome;
    private String turma;
    private int pb;
    private int colorida;
    private int total;

    public Professor() {
    }

    public Professor(Integer id, String nome, String turma, int pb, int colorida, int total) {
        this.id = id;
        this.nome = nome;
        this.turma = turma;
        this.pb = pb;
        this.colorida = colorida;
        this.total = total;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTurma() {
        return turma;
    }

    public void setTurma(String turma) {
        this.turma = turma;
    }

    public int getPb() {
        return pb;
    }

    public void setPb(int pb) {
        this.pb = pb;
    }

    public int getColorida() {
        return colorida;
    }

    public void setColorida(int colorida) {
        this.colorida = colorida;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Professor{id=" + id + ", nome='" + nome + "', turma='" + turma +
                "', pb=" + pb + ", colorida=" + colorida + ", total=" + total + '}';
    }
}
