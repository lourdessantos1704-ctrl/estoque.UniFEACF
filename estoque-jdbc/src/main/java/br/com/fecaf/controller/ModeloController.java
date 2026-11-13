package br.com.fecaf.controller;

import br.com.fecaf.model.Marca;
import br.com.fecaf.model.Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ModeloController {
    private final Connection conexao;

    public ModeloController(Connection conexao) {
        this.conexao = conexao;
    }

    public void inserir(Modelo modelo) throws SQLException {
        String sql = "INSERT INTO modelos (nome, marca_id) VALUES (?, ?)";
        try (PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, modelo.getNome());
            ps.setLong(2, modelo.getMarca().getId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    modelo.setId(rs.getLong(1));
                }
            }
        }
    }

    public List<Modelo> listar() throws SQLException {
        List<Modelo> lista = new ArrayList<>();
        String sql = "SELECT mo.id, mo.nome, ma.id AS marca_id, ma.nome AS marca_nome "
                + "FROM modelos mo JOIN marcas ma ON mo.marca_id = ma.id "
                + "ORDER BY ma.nome, mo.nome";
        try (PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Marca marca = new Marca(rs.getString("marca_nome"));
                marca.setId(rs.getLong("marca_id"));
                Modelo modelo = new Modelo(rs.getString("nome"), marca);
                modelo.setId(rs.getLong("id"));
                lista.add(modelo);
            }
        }
        return lista;
    }

    public boolean existe(long id) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement("SELECT 1 FROM modelos WHERE id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean temVeiculos(long id) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement("SELECT 1 FROM veiculos WHERE modelo_id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean remover(long id) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement("DELETE FROM modelos WHERE id = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
