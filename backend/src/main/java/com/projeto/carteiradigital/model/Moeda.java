package com.projeto.carteiradigital.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "moeda")

public class Moeda {

    @Id
    @Column(name = "id_moeda", nullable = false)
    private Short idMoeda;
    @Column(name = "codigo", length = 10, nullable = false, unique = true)
    private String codigo;
    @Column(name = "nome", length = 50, nullable = false)
    private String nome;
    @Column(name = "tipo", length = 20, nullable = false)
    private String tipo;

    public Moeda() {
    }
    public Moeda(Short idMoeda, String codigo, String nome, String tipo) {
        this.idMoeda = idMoeda;
        this.codigo = codigo;
        this.nome = nome;
        this.tipo = tipo;
    }

    public Short getIdMoeda() {
        return idMoeda;
    }

    public void setIdMoeda(Short idMoeda) {
        this.idMoeda = idMoeda;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Moeda moeda = (Moeda) o;
        return Objects.equals(idMoeda, moeda.idMoeda);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMoeda);
    }
}
