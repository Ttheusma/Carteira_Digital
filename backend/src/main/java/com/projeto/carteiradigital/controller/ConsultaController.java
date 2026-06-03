package com.projeto.carteiradigital.controller;

import com.projeto.carteiradigital.model.SaldoCarteira;
import com.projeto.carteiradigital.model.Transacao;
import com.projeto.carteiradigital.service.ConsultaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carteiras/{endereco}")
public class ConsultaController {

    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @GetMapping("/saldos")
    public ResponseEntity<List<SaldoCarteira>> obterSaldos(@PathVariable String endereco) {
        return ResponseEntity.ok(consultaService.consultarSaldos(endereco));
    }

    @GetMapping("/extrato")
    public ResponseEntity<List<Transacao>> obterExtrato(@PathVariable String endereco) {
        return ResponseEntity.ok(consultaService.consultarExtrato(endereco));
    }
}