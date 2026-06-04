package com.projeto.carteiradigital.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.projeto.carteiradigital.model.Conversao;
import com.projeto.carteiradigital.model.Moeda;
import com.projeto.carteiradigital.repository.ConversaoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ConversaoService {

    private static final int ESCALA = 8;

    private final ConversaoRepository conversaoRepository;
    private final SaldoCarteiraService saldoCarteiraService;
    private final MoedaService moedaService;
    private final RestTemplate restTemplate;

    @Value("${TAXA_CONVERSAO_PERCENTUAL:0.02}")
    private BigDecimal taxaConversaoPercentual;

    public ConversaoService(ConversaoRepository conversaoRepository,
                            SaldoCarteiraService saldoCarteiraService,
                            MoedaService moedaService) {
        this.conversaoRepository = conversaoRepository;
        this.saldoCarteiraService = saldoCarteiraService;
        this.moedaService = moedaService;
        this.restTemplate = new RestTemplate();
    }

    private BigDecimal obterCotacaoCoinbase(String moedaOrigem, String moedaDestino) {
        String url = String.format("https://api.coinbase.com/v2/prices/%s-%s/spot", moedaOrigem, moedaDestino);
        try {
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            if (response != null && response.has("data") && response.get("data").has("amount")) {
                return new BigDecimal(response.get("data").get("amount").asText())
                        .setScale(ESCALA, RoundingMode.HALF_UP);
            }
            throw new RuntimeException("Falha ao analisar o JSON da Coinbase.");
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao comunicar com a API da Coinbase: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void realizarConversao(String enderecoCarteira,
                                   String codigoMoedaOrigem,
                                   String codigoMoedaDestino,
                                   BigDecimal valorOrigem) {

        Moeda moedaOrigem = moedaService.buscarPorCodigo(codigoMoedaOrigem)
                .orElseThrow(() -> new IllegalArgumentException("Moeda de origem não suportada: " + codigoMoedaOrigem));
        Moeda moedaDestino = moedaService.buscarPorCodigo(codigoMoedaDestino)
                .orElseThrow(() -> new IllegalArgumentException("Moeda de destino não suportada: " + codigoMoedaDestino));

        // 1. Cotação da Coinbase
        BigDecimal cotacao = obterCotacaoCoinbase(codigoMoedaOrigem, codigoMoedaDestino);

        // 2. Cálculos com escala explícita — elimina NullPointerException e escala imprevisível
        BigDecimal valorDestinoBruto = valorOrigem.multiply(cotacao)
                .setScale(ESCALA, RoundingMode.HALF_UP);
        BigDecimal valorTaxaOrigem = valorOrigem.multiply(taxaConversaoPercentual)
                .setScale(ESCALA, RoundingMode.HALF_UP);
        BigDecimal valorTotalDebitoOrigem = valorOrigem.add(valorTaxaOrigem)
                .setScale(ESCALA, RoundingMode.HALF_UP);

        // 3. Atualizar saldos (Fail-Fast se saldo insuficiente)
        saldoCarteiraService.subtrairSaldo(enderecoCarteira, moedaOrigem.getIdMoeda(), valorTotalDebitoOrigem);
        saldoCarteiraService.adicionarSaldo(enderecoCarteira, moedaDestino.getIdMoeda(), valorDestinoBruto);

        // 4. Registrar histórico na tabela CONVERSAO
        Conversao conversao = new Conversao();
        conversao.setEnderecoCarteira(enderecoCarteira);
        conversao.setIdMoedaOrigem(moedaOrigem.getIdMoeda());
        conversao.setIdMoedaDestino(moedaDestino.getIdMoeda());
        conversao.setValorOrigem(valorOrigem.setScale(ESCALA, RoundingMode.HALF_UP));
        conversao.setValorDestino(valorDestinoBruto);
        conversao.setTaxaPercentual(taxaConversaoPercentual.setScale(4, RoundingMode.HALF_UP));
        conversao.setTaxaValor(valorTaxaOrigem);
        conversao.setCotacaoUtilizada(cotacao);

        conversaoRepository.save(conversao);
    }
}
