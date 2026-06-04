package com.projeto.carteiradigital.model;

public class Moeda {

    private Short idMoeda;
    private String codigo;
    private String nome;
    private String tipo;

    public Moeda() {
    }

    public Moeda(Short idMoeda, String codigo, String nome, String tipo) {
        this.idMoeda = idMoeda;
        this.codigo = codigo;
        this.nome = nome;
        this.tipo = tipo;
    }

    // Getters e Setters
    public Short getIdMoeda() { return idMoeda; }
    public void setIdMoeda(Short idMoeda) { this.idMoeda = idMoeda; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}