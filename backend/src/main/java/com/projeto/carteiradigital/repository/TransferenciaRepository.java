package com.projeto.carteiradigital.repository;

import com.projeto.carteiradigital.model.Transferencia;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TransferenciaRepository {

    private final JdbcTemplate jdbcTemplate;

    public TransferenciaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Transferencia transferencia) {
        String sql = "INSERT INTO TRANSFERENCIA (endereco_origem, endereco_destino, id_moeda, valor, taxa_valor, data_hora) " +
                     "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        
        jdbcTemplate.update(sql,
                transferencia.getEnderecoOrigem(),
                transferencia.getEnderecoDestino(),
                transferencia.getIdMoeda(),
                transferencia.getValor(),
                transferencia.getTaxaValor());
    }
}