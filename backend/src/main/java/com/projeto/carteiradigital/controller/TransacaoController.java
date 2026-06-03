package com.projeto.carteiradigital.controller;

import com.projeto.carteiradigital.dto.OperacaoFinanceiraDto;
import com.projeto.carteiradigital.model.Transacao;
import com.projeto.carteiradigital.service.TransacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carteiras/{endereco}")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

   
    @PostMapping("/depositos")
    public ResponseEntity<Transacao> depositar(
            @PathVariable String endereco,
            @RequestBody OperacaoFinanceiraDto dto) {
        
        Transacao transacao = transacaoService.depositar(endereco, dto.codigoMoeda(), dto.valor());
        
        return ResponseEntity.ok(transacao);
    }

   
    @PostMapping("/saques")
    public ResponseEntity<Transacao> sacar(
            @PathVariable String endereco,
            @RequestBody OperacaoFinanceiraDto dto) {
        
        Transacao transacao = transacaoService.sacar(endereco, dto.codigoMoeda(), dto.valor());
        
        return ResponseEntity.ok(transacao);
    }
}