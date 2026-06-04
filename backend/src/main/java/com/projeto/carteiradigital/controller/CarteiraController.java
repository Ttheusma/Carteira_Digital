package com.projeto.carteiradigital.controller;

import com.projeto.carteiradigital.model.Carteira;
import com.projeto.carteiradigital.service.CarteiraService;
import com.projeto.carteiradigital.util.CryptoUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/carteiras")
public class CarteiraController {

    private final CarteiraService carteiraService;
    private final CryptoUtils cryptoUtils;

    // Tamanhos lidos do .env — número de bytes; resultado em hex terá o dobro de caracteres
    // PUBLIC_KEY_SIZE=16  → endereço público com 32 caracteres hex
    // PRIVATE_KEY_SIZE=32 → chave privada com 64 caracteres hex
    @Value("${PUBLIC_KEY_SIZE:16}")
    private int publicKeySize;

    @Value("${PRIVATE_KEY_SIZE:32}")
    private int privateKeySize;

    public CarteiraController(CarteiraService carteiraService, CryptoUtils cryptoUtils) {
        this.carteiraService = carteiraService;
        this.cryptoUtils = cryptoUtils;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> criarCarteira() {
        String enderecoPublico = gerarChaveHex(publicKeySize);
        String chavePrivada   = gerarChaveHex(privateKeySize);

        // Armazena apenas o hash SHA-256 — chave privada nunca vai ao banco
        String hash = cryptoUtils.gerarHashSHA256(chavePrivada);
        carteiraService.criarCarteira(enderecoPublico, hash);

        // Chave privada retornada UMA ÚNICA VEZ
        Map<String, String> response = new HashMap<>();
        response.put("enderecoCarteira", enderecoPublico);
        response.put("chavePrivada", chavePrivada);
        response.put("alerta", "ATENÇÃO: Guarde sua chave privada! Ela é sua senha e não poderá ser recuperada.");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{enderecoCarteira}")
    public ResponseEntity<Carteira> buscarCarteira(@PathVariable String enderecoCarteira) {
        Optional<Carteira> carteira = carteiraService.buscarPorEndereco(enderecoCarteira);
        return carteira.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Gera uma string hexadecimal aleatória de comprimento (tamanhoBytes * 2).
     * Usa SecureRandom, adequado para geração de chaves criptográficas.
     */
    private String gerarChaveHex(int tamanhoBytes) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[tamanhoBytes];
        random.nextBytes(bytes);
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
}
