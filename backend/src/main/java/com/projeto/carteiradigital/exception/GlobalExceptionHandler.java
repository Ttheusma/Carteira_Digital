package com.projeto.carteiradigital.exception;

import com.projeto.carteiradigital.dto.ErroPadraoDto;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Regras de negócio violadas (moeda inválida, carteira não encontrada, saldo insuficiente).
     * → HTTP 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroPadraoDto> tratarRegrasDeNegocio(IllegalArgumentException ex,
                                                                HttpServletRequest request) {
        ErroPadraoDto erro = new ErroPadraoDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    /**
     * Saldo insuficiente (RuntimeException lançada pelo SaldoCarteiraService).
     * → HTTP 422 Unprocessable Entity
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErroPadraoDto> tratarRuntimeDeNegocio(RuntimeException ex,
                                                                  HttpServletRequest request) {
        // AcessoNegadoException é subclasse — tratada antes por handler mais específico
        log.warn("Erro de negócio em {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        ErroPadraoDto erro = new ErroPadraoDto(
                LocalDateTime.now(),
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(erro);
    }

    /**
     * Chave privada ausente ou inválida.
     * → HTTP 403 Forbidden
     */
    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<ErroPadraoDto> tratarAcessoNegado(AcessoNegadoException ex,
                                                             HttpServletRequest request) {
        log.warn("Tentativa de acesso negado em {} {}", request.getMethod(), request.getRequestURI());
        ErroPadraoDto erro = new ErroPadraoDto(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erro);
    }

    /**
     * Erros inesperados do sistema (bugs, falha de infraestrutura).
     * → HTTP 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroPadraoDto> tratarErrosInesperados(Exception ex,
                                                                  HttpServletRequest request) {
        log.error("Erro inesperado ao processar {} {}", request.getMethod(), request.getRequestURI(), ex);
        ErroPadraoDto erro = new ErroPadraoDto(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno no servidor. Contate o suporte técnico.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}
