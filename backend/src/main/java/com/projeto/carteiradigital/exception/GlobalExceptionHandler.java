package com.projeto.carteiradigital.exception;

import com.projeto.carteiradigital.dto.ErroPadraoDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroPadraoDto> tratarRegrasDeNegocio(IllegalArgumentException ex, HttpServletRequest request) {
        ErroPadraoDto erro = new ErroPadraoDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(), 
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

   
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroPadraoDto> tratarErrosInesperados(Exception ex, HttpServletRequest request) {
        
        ErroPadraoDto erro = new ErroPadraoDto(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno no servidor. Contate o suporte técnico.", // Mensagem genérica para evitar vazamento de informações sensíveis
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}