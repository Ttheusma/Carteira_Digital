package com.projeto.carteiradigital.controller;

import com.projeto.carteiradigital.dto.CriarCarteiraDto;
import com.projeto.carteiradigital.model.Carteira;
import com.projeto.carteiradigital.service.CarteiraService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carteiras")
public class CarteiraController {

    private final CarteiraService carteiraService;
    
    
    public CarteiraController(CarteiraService carteiraService) {
        this.carteiraService = carteiraService;
    }

    @PostMapping
    public ResponseEntity<Carteira> criarCarteira(@RequestBody CriarCarteiraDto dto) {
        Carteira novaCarteira = carteiraService.criarNovaCarteira(dto.hashChavePrivada());
        return ResponseEntity.status(HttpStatus.CREATED).body(novaCarteira);
    }
}