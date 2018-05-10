package com.example.william.speechpad;

public class Lembrete {

        private int idLembrete, idCategoria, idPrioridade;
        private long  miliSegundosLembrete;
        private String nomeCategoria, notaLembrete, nomePrioridade, dataLembrete, horaLembrete;

    // construtor
    public Lembrete() {
    }

    // setters

    public void setMiliSegundosLembrete(long miliSegundosLembrete) {this.miliSegundosLembrete = miliSegundosLembrete;}

    public void setIdLembrete(int idLembrete) {
        this.idLembrete = idLembrete;
    }

    public void setIdPrioridade(int idPrioridade) {
        this.idPrioridade = idPrioridade;
    }

    public void setNomePrioridade(String nomePrioridade) {
        this.nomePrioridade = nomePrioridade;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public void setNomeCategoria(String nomeCategoria) {
        this.nomeCategoria = nomeCategoria;
    }

    public void setNotaLembrete(String notaLembrete) {
        this.notaLembrete = notaLembrete;
    }

    public void setHoraLembrete(String horaLembrete) {
        this.horaLembrete = horaLembrete;
    }

    public void setDataLembrete(String dataLembrete) {
        this.dataLembrete = dataLembrete;
    }

    // getters

    public long getMiliSegundosLembrete() { return miliSegundosLembrete; }

    public int getIdLembrete() {
        return idLembrete;
    }

    public int getIdPrioridade() {
        return idPrioridade;
    }

    public String getNomePrioridade() {
        return nomePrioridade;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public String getNotaLembrete() {
        return notaLembrete;
    }

    public String getNomeCategoria() {
        return nomeCategoria;
    }

    public String getHoraLembrete() {
        return horaLembrete;
    }

    public String getDataLembrete() {
        return dataLembrete;
    }

}
