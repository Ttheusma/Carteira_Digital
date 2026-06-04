package com.projeto.carteiradigital.repository;

import com.projeto.carteiradigital.model.Conversao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ConversaoRepository {

    private final JdbcTemplate jdbcTemplate;

    public ConversaoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Conversao conversao) {
        String sql = "INSERT INTO CONVERSAO (endereco_carteira, id_moeda_origem, id_moeda_destino, valor_origem, valor_destino, taxa_percentual, taxa_valor, cotacao_utilizada, data_hora) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        
        jdbcTemplate.update(sql,
                conversao.getEnderecoCarteira(),
                conversao.getIdMoedaOrigem(),
                conversao.getIdMoedaDestino(),
                conversao.getValorOrigem(),
                conversao.getValorDestino(),
                conversao.getTaxaPercentual(),
                conversao.getTaxaValor(),
                conversao.getCotacaoUtilizada());
    }
}