
package com.projeto.carteiradigital.dto;

import java.math.BigDecimal;


public record OperacaoFinanceiraDto(String codigoMoeda, BigDecimal valor) {
}