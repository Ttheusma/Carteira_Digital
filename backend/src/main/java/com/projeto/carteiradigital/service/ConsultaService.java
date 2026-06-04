package com.projeto.carteiradigital.service;

import com.projeto.carteiradigital.model.SaldoCarteira;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConsultaService {

    private final JdbcTemplate jdbcTemplate;
    private final SaldoCarteiraService saldoCarteiraService;

    public ConsultaService(JdbcTemplate jdbcTemplate, SaldoCarteiraService saldoCarteiraService) {
        this.jdbcTemplate = jdbcTemplate;
        this.saldoCarteiraService = saldoCarteiraService;
    }

    public List<SaldoCarteira> listarSaldos(String enderecoCarteira) {
        return saldoCarteiraService.listarSaldos(enderecoCarteira);
    }

    // O Motor de Extrato: Une as 3 tabelas diferentes para não quebrar a interface gráfica
    public List<Map<String, Object>> gerarExtrato(String enderecoCarteira) {
        String sql = 
            "SELECT ds.id_movimento AS id, ds.data_hora, ds.tipo AS operacao, m.codigo AS moeda, ds.valor, ds.taxa_valor " +
            "FROM DEPOSITO_SAQUE ds JOIN MOEDA m ON ds.id_moeda = m.id_moeda " +
            "WHERE ds.endereco_carteira = ? " +
            "UNION ALL " +
            "SELECT t.id_transferencia, t.data_hora, 'TRANSFERENCIA_ENVIADA', m.codigo, t.valor, t.taxa_valor " +
            "FROM TRANSFERENCIA t JOIN MOEDA m ON t.id_moeda = m.id_moeda " +
            "WHERE t.endereco_origem = ? " +
            "UNION ALL " +
            "SELECT t.id_transferencia, t.data_hora, 'TRANSFERENCIA_RECEBIDA', m.codigo, t.valor, 0 " +
            "FROM TRANSFERENCIA t JOIN MOEDA m ON t.id_moeda = m.id_moeda " +
            "WHERE t.endereco_destino = ? " +
            "UNION ALL " +
            "SELECT c.id_conversao, c.data_hora, 'CONVERSAO', m.codigo, c.valor_origem, c.taxa_valor " +
            "FROM CONVERSAO c JOIN MOEDA m ON c.id_moeda_origem = m.id_moeda " +
            "WHERE c.endereco_carteira = ? " +
            "ORDER BY data_hora DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> tx = new HashMap<>();
            tx.put("id", rs.getLong("id"));
            tx.put("dataHora", rs.getTimestamp("data_hora").toLocalDateTime());
            tx.put("tipoOperacao", rs.getString("operacao"));
            
            // O frontend espera o formato tx.moeda.codigo para não quebrar a tabela
            Map<String, String> moeda = new HashMap<>();
            moeda.put("codigo", rs.getString("moeda"));
            tx.put("moeda", moeda);
            
            tx.put("valor", rs.getBigDecimal("valor"));
            tx.put("taxaCobrada", rs.getBigDecimal("taxa_valor"));
            return tx;
        }, enderecoCarteira, enderecoCarteira, enderecoCarteira, enderecoCarteira);
    }
}