package com.projeto.carteiradigital.service;

import com.projeto.carteiradigital.model.Moeda;
import com.projeto.carteiradigital.model.Transferencia;
import com.projeto.carteiradigital.repository.TransferenciaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TransferenciaService {

    private static final int ESCALA = 8;

    private final TransferenciaRepository transferenciaRepository;
    private final SaldoCarteiraService saldoCarteiraService;
    private final MoedaService moedaService;

    @Value("${TAXA_TRANSFERENCIA_PERCENTUAL:0.01}")
    private BigDecimal taxaTransferenciaPercentual;

    public TransferenciaService(TransferenciaRepository transferenciaRepository,
                                SaldoCarteiraService saldoCarteiraService,
                                MoedaService moedaService) {
        this.transferenciaRepository = transferenciaRepository;
        this.saldoCarteiraService = saldoCarteiraService;
        this.moedaService = moedaService;
    }

    @Transactional
    public void realizarTransferencia(String enderecoOrigem, String enderecoDestino,
                                       String codigoMoeda, BigDecimal valor) {
        Moeda moeda = moedaService.buscarPorCodigo(codigoMoeda)
                .orElseThrow(() -> new IllegalArgumentException("Moeda não suportada: " + codigoMoeda));

        // Taxa com escala explícita — elimina escala imprevisível de BigDecimal
        BigDecimal valorTaxa = valor.multiply(taxaTransferenciaPercentual)
                .setScale(ESCALA, RoundingMode.HALF_UP);
        BigDecimal valorTotalDebito = valor.add(valorTaxa)
                .setScale(ESCALA, RoundingMode.HALF_UP);

        // Débito da origem (Fail-Fast se saldo insuficiente para valor + taxa)
        saldoCarteiraService.subtrairSaldo(enderecoOrigem, moeda.getIdMoeda(), valorTotalDebito);

        // Crédito no destino: valor líquido sem taxa
        saldoCarteiraService.adicionarSaldo(enderecoDestino, moeda.getIdMoeda(),
                valor.setScale(ESCALA, RoundingMode.HALF_UP));

        Transferencia transferencia = new Transferencia();
        transferencia.setEnderecoOrigem(enderecoOrigem);
        transferencia.setEnderecoDestino(enderecoDestino);
        transferencia.setIdMoeda(moeda.getIdMoeda());
        transferencia.setValor(valor.setScale(ESCALA, RoundingMode.HALF_UP));
        transferencia.setTaxaValor(valorTaxa);

        transferenciaRepository.save(transferencia);
    }
}
