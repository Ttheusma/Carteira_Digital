package com.projeto.carteiradigital.dto;

import java.math.BigDecimal;

public record TransferenciaDto(
        String enderecoDestino, 
        String codigoMoeda, 
        BigDecimal valor
) {}