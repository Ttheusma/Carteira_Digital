package com.projeto.carteiradigital.dto;

import java.math.BigDecimal;

public record ConversaoDto(
        String codigoMoedaOrigem,
        String codigoMoedaDestino,
        BigDecimal valor,
        BigDecimal cotacao
) {}