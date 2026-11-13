package br.com.fecaf.controller;

import br.com.fecaf.model.Marca;
import br.com.fecaf.model.Modelo;
import br.com.fecaf.model.StatusVeiculo;
import br.com.fecaf.model.Veiculo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VeiculoController {
    private final Connection conexao;

    private static final String SELECT_BASE =
            "SELECT v.id, v.ano, v.cor, v.preco, v.quilometragem, v.status, "
                    + "mo.id AS modelo_id, mo.nome AS modelo_nome, "
                    + "ma.id AS marca_id, ma.nome AS marca_nome "
                    + "FROM veiculos v "
                    + "JOIN modelos mo ON v.modelo_id = mo.id "
                    + "JOIN marcas ma ON mo.marca_id = ma.id ";

    public VeiculoController(Connection conexao) {
        this.conexao = conexao;
    }

    public void inserir(Veiculo v) throws SQLException {
        String sql = "INSERT INTO veiculos (modelo_id, ano, cor, preco, quilometragem, status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, v.getModelo().getId());
            ps.setInt(2, v.getAno());
            ps.setString(3, v.getCor());
            ps.setBigDecimal(4, v.getPreco());
            ps.setInt(5, v.getQuilometragem());
            ps.setString(6, v.getStatus().name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    v.setId(rs.getLong(1));
                }
            }
        }
    }

    public List<Veiculo> listar(Long marcaId, Long modeloId, BigDecimal precoMin,
                                BigDecimal precoMax, Integer ano, StatusVeiculo status) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_BASE).append("WHERE 1=1 ");
        List<Object> parametros = new ArrayList<>();

        if (marcaId != null) {
            sql.append("AND ma.id = ? ");
            parametros.add(marcaId);
        }
        if (modeloId != null) {
            sql.append("AND mo.id = ? ");
            parametros.add(modeloId);
        }
        if (precoMin != null) {
            sql.append("AND v.preco >= ? ");
            parametros.add(precoMin);
        }
        if (precoMax != null) {
            sql.append("AND v.preco <= ? ");
            parametros.add(precoMax);
        }
        if (ano != null) {
            sql.append("AND v.ano = ? ");
            parametros.add(ano);
        }
        if (status != null) {
            sql.append("AND v.status = ? ");
            parametros.add(status.name());
        }
        sql.append("ORDER BY v.id");

        List<Veiculo> lista = new ArrayList<>();
        try (PreparedStatement ps = conexao.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(montar(rs));
                }
            }
        }
        return lista;
    }

    public List<Veiculo> listarTodos() throws SQLException {
        return listar(null, null, null, null, null, null);
    }

    public Veiculo buscarPorId(long id) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement(SELECT_BASE + "WHERE v.id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return montar(rs);
                }
            }
        }
        return null;
    }

    public void atualizar(Veiculo v) throws SQLException {
        String sql = "UPDATE veiculos SET preco = ?, quilometragem = ?, status = ? WHERE id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setBigDecimal(1, v.getPreco());
            ps.setInt(2, v.getQuilometragem());
            ps.setString(3, v.getStatus().name());
            ps.setLong(4, v.getId());
            ps.executeUpdate();
        }
    }

    public boolean remover(long id) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement("DELETE FROM veiculos WHERE id = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Veiculo montar(ResultSet rs) throws SQLException {
        Marca marca = new Marca(rs.getString("marca_nome"));
        marca.setId(rs.getLong("marca_id"));

        Modelo modelo = new Modelo(rs.getString("modelo_nome"), marca);
        modelo.setId(rs.getLong("modelo_id"));

        Veiculo v = new Veiculo(modelo, rs.getInt("ano"), rs.getString("cor"),
                rs.getBigDecimal("preco"), rs.getInt("quilometragem"),
                StatusVeiculo.valueOf(rs.getString("status")));
        v.setId(rs.getLong("id"));
        return v;
    }
}
