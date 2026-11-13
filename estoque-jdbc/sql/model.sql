CREATE DATABASE IF NOT EXISTS estoque_veiculos
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE estoque_veiculos;

CREATE TABLE IF NOT EXISTS marcas (
    id    BIGINT       NOT NULL AUTO_INCREMENT,
    nome  VARCHAR(60)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_marcas_nome (nome)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS modelos (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    nome      VARCHAR(80)  NOT NULL,
    marca_id  BIGINT       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_modelos_marca FOREIGN KEY (marca_id) REFERENCES marcas (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS veiculos (
    id             BIGINT         NOT NULL AUTO_INCREMENT,
    modelo_id      BIGINT         NOT NULL,
    ano            INT            NOT NULL,
    cor            VARCHAR(30)    NOT NULL,
    preco          DECIMAL(12,2)  NOT NULL,
    quilometragem  INT            NOT NULL,
    status         VARCHAR(20)    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_veiculos_modelo FOREIGN KEY (modelo_id) REFERENCES modelos (id)
) ENGINE=InnoDB;

SELECT * FROM marcas;
SELECT * FROM modelos;
SELECT * FROM veiculos;
