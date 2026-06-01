package com.projeto.carteiradigital.model;


import java.io.Serializable;
import java.util.Objects;

public class SaldoCarteiraId implements Serializable {
    private String carteira;
    private Short moeda;

    public SaldoCarteiraId() {
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaldoCarteiraId that = (SaldoCarteiraId) o;
        return Objects.equals(carteira, that.carteira) && Objects.equals(moeda, that.moeda);
    }

    @Override
    public int hashCode() {
        return Objects.hash(carteira, moeda);
    }

   
}