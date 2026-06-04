package com.projeto.carteiradigital.service;

import com.projeto.carteiradigital.model.Moeda;
import com.projeto.carteiradigital.repository.ConversaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class ConversaoServiceTest {

    @Mock
    private ConversaoRepository conversaoRepository;

    @Mock
    private SaldoCarteiraService saldoCarteiraService;

    @Mock
    private MoedaService moedaService;

    @InjectMocks
    private ConversaoService conversaoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Simular a injeção da variável de ambiente do Docker (.env)
        ReflectionTestUtils.setField(conversaoService, "taxaConversaoPercentual", new BigDecimal("0.02"));
    }

    @Test
    void deveLancarExcecaoQuandoMoedaOrigemNaoExistir() {
        when(moedaService.buscarPorCodigo("XYZ")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                conversaoService.realizarConversao("carteira-123", "XYZ", "USD", new BigDecimal("100")));
    }

    @Test
    void deveLancarExcecaoQuandoMoedaDestinoNaoExistir() {
        Moeda btc = new Moeda((short) 1, "BTC", "Bitcoin", "CRYPTO");
        when(moedaService.buscarPorCodigo("BTC")).thenReturn(Optional.of(btc));
        when(moedaService.buscarPorCodigo("XYZ")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                conversaoService.realizarConversao("carteira-123", "BTC", "XYZ", new BigDecimal("100")));
    }
}