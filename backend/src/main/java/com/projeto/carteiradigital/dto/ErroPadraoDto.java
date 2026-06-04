package com.projeto.carteiradigital.dto;

import java.time.LocalDateTime;

public record ErroPadraoDto(
        LocalDateTime timestamp,
        Integer status,
        String erro,
        String caminho
) {}