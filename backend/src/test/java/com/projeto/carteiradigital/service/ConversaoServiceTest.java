package com.projeto.carteiradigital.service;

import com.projeto.carteiradigital.model.*;
import com.projeto.carteiradigital.repository.ConversaoRepository;
import com.projeto.carteiradigital.repository.SaldoCarteiraRepository;
import com.projeto.carteiradigital.repository.TransacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversaoServiceTest {

    @Mock private ConversaoRepository conversaoRepository;
    @Mock private TransacaoRepository transacaoRepository;
    @Mock private SaldoCarteiraRepository saldoCarteiraRepository;
    @Mock private CarteiraService carteiraService;
    @Mock private MoedaService moedaService;

    @InjectMocks
    private ConversaoService conversaoService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(conversaoService, "taxaConversaoPercentual", new BigDecimal("0.02"));
    }

   @Test
    @DisplayName("Deve converter BRL para USD com sucesso, calculando a taxa exata de 2%")
    void deveConverterComSucessoECalcularTaxaExata() {
        String endereco = "carteira-123";
        Carteira carteiraMock = mock(Carteira.class);
        Moeda moedaBrl = mock(Moeda.class);
        Moeda moedaUsd = mock(Moeda.class);

        SaldoCarteira saldoOrigem = new SaldoCarteira(carteiraMock, moedaBrl, new BigDecimal("100.00"));
        
        SaldoCarteira saldoDestino = new SaldoCarteira(carteiraMock, moedaUsd, BigDecimal.ZERO);

        when(carteiraService.buscarCarteiraSegura(endereco)).thenReturn(carteiraMock);
        when(moedaService.buscarPorCodigoSeguro("BRL")).thenReturn(moedaBrl);
        when(moedaService.buscarPorCodigoSeguro("USD")).thenReturn(moedaUsd);
        when(saldoCarteiraRepository.findByCarteira_EnderecoCarteiraAndMoeda_Codigo(endereco, "BRL"))
                .thenReturn(Optional.of(saldoOrigem));
        when(saldoCarteiraRepository.findByCarteira_EnderecoCarteiraAndMoeda_Codigo(endereco, "USD"))
                .thenReturn(Optional.of(saldoDestino));

        BigDecimal valorConverter = new BigDecimal("100.00");
        BigDecimal cotacao = new BigDecimal("0.20");

  
        Conversao recibo = conversaoService.converter(endereco, "BRL", "USD", valorConverter, cotacao);

       
        assertNotNull(recibo);
        
        
        assertEquals(new BigDecimal("20.00000000"), recibo.getValorDestino());
        
        
        assertEquals(new BigDecimal("0.00400000"), recibo.getTaxaValor());

        
        assertEquals(new BigDecimal("0.00"), saldoOrigem.getSaldo()); 
        assertEquals(new BigDecimal("19.99600000"), saldoDestino.getSaldo());

        
        verify(saldoCarteiraRepository, times(2)).save(any(SaldoCarteira.class));
        verify(conversaoRepository, times(1)).save(any(Conversao.class));
        verify(transacaoRepository, times(2)).save(any(Transacao.class));
    }

    @Test
    @DisplayName("Deve bloquear conversão se o saldo de origem for insuficiente (Fail-Fast)")
    void deveLancarExceptionPorSaldoInsuficiente() {
     
        String endereco = "carteira-123";
        Carteira carteiraMock = mock(Carteira.class);
        Moeda moedaBrl = mock(Moeda.class);
        Moeda moedaUsd = mock(Moeda.class);

      
        SaldoCarteira saldoOrigem = new SaldoCarteira(carteiraMock, moedaBrl, new BigDecimal("10.00"));

        when(carteiraService.buscarCarteiraSegura(endereco)).thenReturn(carteiraMock);
        when(moedaService.buscarPorCodigoSeguro("BRL")).thenReturn(moedaBrl);
        when(moedaService.buscarPorCodigoSeguro("USD")).thenReturn(moedaUsd);
        when(saldoCarteiraRepository.findByCarteira_EnderecoCarteiraAndMoeda_Codigo(endereco, "BRL"))
                .thenReturn(Optional.of(saldoOrigem));

        
        BigDecimal valorConverter = new BigDecimal("100.00");
        BigDecimal cotacao = new BigDecimal("0.20");

      
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            conversaoService.converter(endereco, "BRL", "USD", valorConverter, cotacao)
        );

        assertEquals("Operação Negada: Saldo insuficiente para realizar a conversão.", exception.getMessage());
        
 
        verify(saldoCarteiraRepository, never()).save(any());
        verify(conversaoRepository, never()).save(any());
    }
}