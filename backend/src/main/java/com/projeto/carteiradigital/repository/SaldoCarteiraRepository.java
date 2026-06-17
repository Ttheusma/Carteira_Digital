package com.projeto.carteiradigital.repository;

import com.projeto.carteiradigital.model.SaldoCarteira;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SaldoCarteiraRepository {

    private final JdbcTemplate jdbcTemplate;

    public SaldoCarteiraRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<SaldoCarteira> rowMapper = (rs, rowNum) -> {
        SaldoCarteira s = new SaldoCarteira();
        s.setEnderecoCarteira(rs.getString("endereco_carteira"));
        s.setIdMoeda(rs.getShort("id_moeda"));
        s.setSaldo(rs.getBigDecimal("saldo"));
        s.setDataAtualizacao(rs.getTimestamp("data_atualizacao").toLocalDateTime());
        return s;
    };

    public Optional<SaldoCarteira> findById(String enderecoCarteira, Short idMoeda) {
        String sql = "SELECT * FROM SALDO_CARTEIRA WHERE endereco_carteira = ? AND id_moeda = ?";
        return jdbcTemplate.query(sql, rowMapper, enderecoCarteira, idMoeda).stream().findFirst();
    }

    public List<SaldoCarteira> findAllByEnderecoCarteira(String enderecoCarteira) {
        String sql = "SELECT * FROM SALDO_CARTEIRA WHERE endereco_carteira = ?";
        return jdbcTemplate.query(sql, rowMapper, enderecoCarteira);
    }

    public void saveOrUpdate(SaldoCarteira saldo) {
        String sql = "INSERT INTO SALDO_CARTEIRA (endereco_carteira, id_moeda, saldo, data_atualizacao) " +
                     "VALUES (?, ?, ?, CURRENT_TIMESTAMP) " +
                     "ON CONFLICT (endereco_carteira, id_moeda) " +
                     "DO UPDATE SET saldo = EXCLUDED.saldo, data_atualizacao = CURRENT_TIMESTAMP";
        
        jdbcTemplate.update(sql, saldo.getEnderecoCarteira(), saldo.getIdMoeda(), saldo.getSaldo());
    }
}