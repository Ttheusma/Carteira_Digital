package com.projeto.carteiradigital.controller;

import com.projeto.carteiradigital.dto.ConversaoDto;
import com.projeto.carteiradigital.dto.OperacaoFinanceiraDto;
import com.projeto.carteiradigital.dto.TransferenciaDto;
import com.projeto.carteiradigital.exception.AcessoNegadoException;
import com.projeto.carteiradigital.model.Carteira;
import com.projeto.carteiradigital.service.*;
import com.projeto.carteiradigital.util.CryptoUtils;
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
    private final CryptoUtils cryptoUtils;

    public TransacaoController(DepositoSaqueService depositoSaqueService,
                               TransferenciaService transferenciaService,
                               ConversaoService conversaoService,
                               CarteiraService carteiraService,
                               CryptoUtils cryptoUtils) {
        this.depositoSaqueService = depositoSaqueService;
        this.transferenciaService = transferenciaService;
        this.conversaoService = conversaoService;
        this.carteiraService = carteiraService;
        this.cryptoUtils = cryptoUtils;
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
        validarChavePrivada(enderecoCarteira, dto.getChavePrivada());
        depositoSaqueService.realizarSaque(enderecoCarteira, dto.getCodigoMoeda(), dto.getValor());
        return ResponseEntity.ok(Map.of("mensagem", "Saque realizado com sucesso!"));
    }

    // --- TRANSFERÊNCIAS (Requer Chave Privada) ---
    @PostMapping("/{enderecoOrigem}/transferencias")
    public ResponseEntity<?> transferir(@PathVariable String enderecoOrigem,
                                        @RequestBody TransferenciaDto dto) {
        validarChavePrivada(enderecoOrigem, dto.getChavePrivada());
        transferenciaService.realizarTransferencia(enderecoOrigem, dto.getEnderecoDestino(),
                dto.getCodigoMoeda(), dto.getValor());
        return ResponseEntity.ok(Map.of("mensagem", "Transferência realizada com sucesso!"));
    }

    // --- CONVERSÕES (Requer Chave Privada) ---
    @PostMapping("/{enderecoCarteira}/conversoes")
    public ResponseEntity<?> converter(@PathVariable String enderecoCarteira,
                                       @RequestBody ConversaoDto dto) {
        validarChavePrivada(enderecoCarteira, dto.getChavePrivada());
        conversaoService.realizarConversao(enderecoCarteira, dto.getMoedaOrigem(),
                dto.getMoedaDestino(), dto.getValorOrigem());
        return ResponseEntity.ok(Map.of("mensagem", "Conversão realizada com sucesso!"));
    }

    // ================================================================
    // MOTOR DE SEGURANÇA — lança AcessoNegadoException (HTTP 403)
    // ================================================================
    private void validarChavePrivada(String enderecoCarteira, String chavePrivadaFornecida) {
        if (chavePrivadaFornecida == null || chavePrivadaFornecida.isBlank()) {
            throw new AcessoNegadoException("Acesso Negado: chave privada é obrigatória para operações de saída.");
        }

        Carteira carteira = carteiraService.buscarPorEndereco(enderecoCarteira)
                .orElseThrow(() -> new IllegalArgumentException("Carteira não encontrada: " + enderecoCarteira));

        String hashFornecido = cryptoUtils.gerarHashSHA256(chavePrivadaFornecida);

        if (!carteira.getHashChavePrivada().equals(hashFornecido)) {
            throw new AcessoNegadoException("Assinatura Inválida: a chave fornecida não corresponde a esta carteira.");
        }
    }
}
