package br.com.fecaf.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexao {
    private String servidor, banco, user, password;
    public Connection conexao;

    public Conexao() {
        this.servidor = "localhost";
        this.banco = "estoque_veiculos";
        // COLOQUE AQUI O USUARIO E A SENHA DO SEU MYSQL
        this.user = "root";
        this.password = "28062007";
    }

    public Connection conectar() throws SQLException {
        if (conexao == null || conexao.isClosed()) {
            String url = "jdbc:mysql://" + servidor + ":3306/" + banco
                    + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo";
            conexao = DriverManager.getConnection(url, user, password);
        }
        return conexao;
    }

    public void criarBancoETabelas() throws SQLException {
        String urlServidor = "jdbc:mysql://" + servidor + ":3306/"
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo";
        try (Connection c = DriverManager.getConnection(urlServidor, user, password);
             Statement st = c.createStatement()) {
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS " + banco
                    + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        }

        try (Statement st = conectar().createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS marcas ("
                    + "id BIGINT NOT NULL AUTO_INCREMENT, "
                    + "nome VARCHAR(60) NOT NULL, "
                    + "PRIMARY KEY (id), UNIQUE KEY uk_marcas_nome (nome)) ENGINE=InnoDB");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS modelos ("
                    + "id BIGINT NOT NULL AUTO_INCREMENT, "
                    + "nome VARCHAR(80) NOT NULL, "
                    + "marca_id BIGINT NOT NULL, "
                    + "PRIMARY KEY (id), "
                    + "CONSTRAINT fk_modelos_marca FOREIGN KEY (marca_id) REFERENCES marcas (id)) ENGINE=InnoDB");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS veiculos ("
                    + "id BIGINT NOT NULL AUTO_INCREMENT, "
                    + "modelo_id BIGINT NOT NULL, "
                    + "ano INT NOT NULL, "
                    + "cor VARCHAR(30) NOT NULL, "
                    + "preco DECIMAL(12,2) NOT NULL, "
                    + "quilometragem INT NOT NULL, "
                    + "status VARCHAR(20) NOT NULL, "
                    + "PRIMARY KEY (id), "
                    + "CONSTRAINT fk_veiculos_modelo FOREIGN KEY (modelo_id) REFERENCES modelos (id)) ENGINE=InnoDB");
        }
    }
}
