package com.projeto.carteiradigital.repository;

import com.projeto.carteiradigital.model.Carteira;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CarteiraRepository {

    private final JdbcTemplate jdbcTemplate;

    public CarteiraRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper ensina o Spring a converter a linha do SQL de volta para o Objeto Java
    private final RowMapper<Carteira> rowMapper = (rs, rowNum) -> {
        Carteira c = new Carteira();
        c.setEnderecoCarteira(rs.getString("endereco_carteira"));
        c.setHashChavePrivada(rs.getString("hash_chave_privada"));
        c.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
        c.setStatus(rs.getString("status"));
        return c;
    };

    public Carteira save(Carteira carteira) {
        String sql = "INSERT INTO CARTEIRA (endereco_carteira, hash_chave_privada, data_criacao, status) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, 
                carteira.getEnderecoCarteira(), 
                carteira.getHashChavePrivada(), 
                carteira.getDataCriacao(), 
                carteira.getStatus());
        return carteira;
    }

    public Optional<Carteira> findById(String enderecoCarteira) {
        String sql = "SELECT * FROM CARTEIRA WHERE endereco_carteira = ?";
        return jdbcTemplate.query(sql, rowMapper, enderecoCarteira).stream().findFirst();
    }
}