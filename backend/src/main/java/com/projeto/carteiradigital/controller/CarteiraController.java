package com.projeto.carteiradigital.controller;

import com.projeto.carteiradigital.dto.CriarCarteiraDto;
import com.projeto.carteiradigital.model.Carteira;
import com.projeto.carteiradigital.model.SaldoCarteira;
import com.projeto.carteiradigital.service.CarteiraService;
import com.projeto.carteiradigital.service.SaldoCarteiraService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carteiras")
public class CarteiraController {

    private final CarteiraService carteiraService;
    private final SaldoCarteiraService saldoCarteiraService;

    
    public CarteiraController(CarteiraService carteiraService, SaldoCarteiraService saldoCarteiraService) {
        this.carteiraService = carteiraService;
        this.saldoCarteiraService = saldoCarteiraService;
    }

    
    @PostMapping
    public ResponseEntity<Carteira> criarCarteira(@RequestBody CriarCarteiraDto dto) {
        
        Carteira novaCarteira = carteiraService.criarNovaCarteira(dto.hashChavePrivada());
        
       
        return ResponseEntity.status(HttpStatus.CREATED).body(novaCarteira);
    }

    
    @GetMapping("/{endereco}/saldos")
    public ResponseEntity<List<SaldoCarteira>> consultarSaldos(@PathVariable String endereco) {
        List<SaldoCarteira> saldos = saldoCarteiraService.buscarSaldosDaCarteira(endereco);
        
        
        return ResponseEntity.ok(saldos);
    }
}