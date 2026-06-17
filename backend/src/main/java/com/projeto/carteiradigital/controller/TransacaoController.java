package com.projeto.carteiradigital.controller;

import com.projeto.carteiradigital.dto.ConversaoDto;
import com.projeto.carteiradigital.dto.OperacaoFinanceiraDto;
import com.projeto.carteiradigital.dto.TransferenciaDto;
import com.projeto.carteiradigital.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/carteiras")
public class TransacaoController {

    private final DepositoSaqueService depositoSaqueService;
    private final TransferenciaService transferenciaService;
    private final ConversaoService conversaoService;
    private final CarteiraService carteiraService;

    public TransacaoController(DepositoSaqueService depositoSaqueService,
                               TransferenciaService transferenciaService,
                               ConversaoService conversaoService,
                               CarteiraService carteiraService) {
        this.depositoSaqueService = depositoSaqueService;
        this.transferenciaService = transferenciaService;
        this.conversaoService = conversaoService;
        this.carteiraService = carteiraService;
    }

    // --- DEPÓSITOS (Sem validação, entrada livre) ---
    @PostMapping("/{enderecoCarteira}/depositos")
    public ResponseEntity<?> depositar(@PathVariable String enderecoCarteira,
                                       @RequestBody OperacaoFinanceiraDto dto) {
        depositoSaqueService.realizarDeposito(enderecoCarteira, dto.getCodigoMoeda(), dto.getValor());
        return ResponseEntity.ok(Map.of("mensagem", "Depósito realizado com sucesso!"));
    }

    // --- SAQUES (Requer Chave Privada) ---
    @PostMapping("/{enderecoCarteira}/saques")
    public ResponseEntity<?> sacar(@PathVariable String enderecoCarteira,
                                   @RequestBody OperacaoFinanceiraDto dto) {
        carteiraService.validarAcesso(enderecoCarteira, dto.getChavePrivada());
        depositoSaqueService.realizarSaque(enderecoCarteira, dto.getCodigoMoeda(), dto.getValor());
        return ResponseEntity.ok(Map.of("mensagem", "Saque realizado com sucesso!"));
    }

    // --- TRANSFERÊNCIAS (Requer Chave Privada) ---
    @PostMapping("/{enderecoOrigem}/transferencias")
    public ResponseEntity<?> transferir(@PathVariable String enderecoOrigem,
                                        @RequestBody TransferenciaDto dto) {
        carteiraService.validarAcesso(enderecoOrigem, dto.getChavePrivada());
        transferenciaService.realizarTransferencia(enderecoOrigem, dto.getEnderecoDestino(),
                dto.getCodigoMoeda(), dto.getValor());
        return ResponseEntity.ok(Map.of("mensagem", "Transferência realizada com sucesso!"));
    }

    // --- CONVERSÕES (Requer Chave Privada) ---
    @PostMapping("/{enderecoCarteira}/conversoes")
    public ResponseEntity<?> converter(@PathVariable String enderecoCarteira,
                                       @RequestBody ConversaoDto dto) {
        carteiraService.validarAcesso(enderecoCarteira, dto.getChavePrivada());
        conversaoService.realizarConversao(enderecoCarteira, dto.getMoedaOrigem(),
                dto.getMoedaDestino(), dto.getValorOrigem());
        return ResponseEntity.ok(Map.of("mensagem", "Conversão realizada com sucesso!"));
    }
}
