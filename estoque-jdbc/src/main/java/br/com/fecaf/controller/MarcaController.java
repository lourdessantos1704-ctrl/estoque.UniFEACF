package br.com.fecaf.controller;

import br.com.fecaf.model.Marca;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MarcaController {
    private final Connection conexao;

    public MarcaController(Connection conexao) {
        this.conexao = conexao;
    }

    public void inserir(Marca marca) throws SQLException {
        String sql = "INSERT INTO marcas (nome) VALUES (?)";
        try (PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, marca.getNome());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    marca.setId(rs.getLong(1));
                }
            }
        }
    }

    public List<Marca> listar() throws SQLException {
        List<Marca> lista = new ArrayList<>();
        String sql = "SELECT id, nome FROM marcas ORDER BY nome";
        try (PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Marca m = new Marca(rs.getString("nome"));
                m.setId(rs.getLong("id"));
                lista.add(m);
            }
        }
        return lista;
    }

    public boolean existe(long id) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement("SELECT 1 FROM marcas WHERE id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
