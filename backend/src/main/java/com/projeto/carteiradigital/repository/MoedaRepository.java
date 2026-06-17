package com.projeto.carteiradigital.repository;

import com.projeto.carteiradigital.model.Moeda;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MoedaRepository {

    private final JdbcTemplate jdbcTemplate;

    public MoedaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Moeda> rowMapper = (rs, rowNum) -> {
        Moeda m = new Moeda();
        m.setIdMoeda(rs.getShort("id_moeda"));
        m.setCodigo(rs.getString("codigo"));
        m.setNome(rs.getString("nome"));
        m.setTipo(rs.getString("tipo"));
        return m;
    };

    public Optional<Moeda> findByCodigo(String codigo) {
        String sql = "SELECT * FROM MOEDA WHERE codigo = ?";
        return jdbcTemplate.query(sql, rowMapper, codigo).stream().findFirst();
    }
}