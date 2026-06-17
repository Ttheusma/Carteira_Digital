package com.projeto.carteiradigital.controller;

import com.projeto.carteiradigital.model.SaldoCarteira;
import com.projeto.carteiradigital.service.ConsultaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/carteiras")
public class ConsultaController {

    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @GetMapping("/{enderecoCarteira}/saldos")
    public ResponseEntity<List<SaldoCarteira>> consultarSaldos(@PathVariable String enderecoCarteira) {
        return ResponseEntity.ok(consultaService.listarSaldos(enderecoCarteira));
    }

    @GetMapping("/{enderecoCarteira}/extrato")
    public ResponseEntity<List<Map<String, Object>>> consultarExtrato(@PathVariable String enderecoCarteira) {
        return ResponseEntity.ok(consultaService.gerarExtrato(enderecoCarteira));
    }
}