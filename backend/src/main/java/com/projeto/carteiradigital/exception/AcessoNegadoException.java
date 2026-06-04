package com.projeto.carteiradigital.exception;

/**
 * Lançada quando a chave privada fornecida é inválida ou ausente.
 * Mapeada pelo GlobalExceptionHandler para HTTP 403 Forbidden.
 */
public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
