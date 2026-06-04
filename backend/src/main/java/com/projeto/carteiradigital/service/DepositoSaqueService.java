package com.projeto.carteiradigital.service;

import com.projeto.carteiradigital.model.DepositoSaque;
import com.projeto.carteiradigital.model.Moeda;
import com.projeto.carteiradigital.repository.DepositoSaqueRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class DepositoSaqueService {

    private static final int ESCALA = 8;

    private final DepositoSaqueRepository depositoSaqueRepository;
    private final SaldoCarteiraService saldoCarteiraService;
    private final MoedaService moedaService;

    @Value("${TAXA_SAQUE_PERCENTUAL:0.01}")
    private BigDecimal taxaSaquePercentual;

    public DepositoSaqueService(DepositoSaqueRepository depositoSaqueRepository,
                                SaldoCarteiraService saldoCarteiraService,
                                MoedaService moedaService) {
        this.depositoSaqueRepository = depositoSaqueRepository;
        this.saldoCarteiraService = saldoCarteiraService;
        this.moedaService = moedaService;
    }

    @Transactional
    public void realizarDeposito(String enderecoCarteira, String codigoMoeda, BigDecimal valor) {
        Moeda moeda = moedaService.buscarPorCodigo(codigoMoeda)
                .orElseThrow(() -> new IllegalArgumentException("Moeda não suportada: " + codigoMoeda));

        DepositoSaque deposito = new DepositoSaque();
        deposito.setEnderecoCarteira(enderecoCarteira);
        deposito.setIdMoeda(moeda.getIdMoeda());
        deposito.setTipo("DEPOSITO");
        deposito.setValor(valor.setScale(ESCALA, RoundingMode.HALF_UP));
        deposito.setTaxaValor(BigDecimal.ZERO.setScale(ESCALA));

        depositoSaqueRepository.save(deposito);

        saldoCarteiraService.adicionarSaldo(enderecoCarteira, moeda.getIdMoeda(),
                valor.setScale(ESCALA, RoundingMode.HALF_UP));
    }

    @Transactional
    public void realizarSaque(String enderecoCarteira, String codigoMoeda, BigDecimal valor) {
        Moeda moeda = moedaService.buscarPorCodigo(codigoMoeda)
                .orElseThrow(() -> new IllegalArgumentException("Moeda não suportada: " + codigoMoeda));

        // Taxa calculada com escala explícita — evita NullPointerException e escala imprevisível
        BigDecimal valorTaxa = valor.multiply(taxaSaquePercentual)
                .setScale(ESCALA, RoundingMode.HALF_UP);
        BigDecimal valorTotalDescontado = valor.add(valorTaxa)
                .setScale(ESCALA, RoundingMode.HALF_UP);

        // Fail-Fast: lança exceção se saldo insuficiente (inclui taxa)
        saldoCarteiraService.subtrairSaldo(enderecoCarteira, moeda.getIdMoeda(), valorTotalDescontado);

        DepositoSaque saque = new DepositoSaque();
        saque.setEnderecoCarteira(enderecoCarteira);
        saque.setIdMoeda(moeda.getIdMoeda());
        saque.setTipo("SAQUE");
        saque.setValor(valor.setScale(ESCALA, RoundingMode.HALF_UP));
        saque.setTaxaValor(valorTaxa);

        depositoSaqueRepository.save(saque);
    }
}
