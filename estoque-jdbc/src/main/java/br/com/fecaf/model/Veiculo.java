package br.com.fecaf.model;

import java.math.BigDecimal;

public class Veiculo extends EntidadeBase {
    private Modelo modelo;
    private int ano;
    private String cor;
    private BigDecimal preco;
    private int quilometragem;
    private StatusVeiculo status;

    public Veiculo() {
    }

    public Veiculo(Modelo modelo, int ano, String cor, BigDecimal preco,
                   int quilometragem, StatusVeiculo status) {
        this.modelo = modelo;
        this.ano = ano;
        this.cor = cor;
        this.preco = preco;
        this.quilometragem = quilometragem;
        this.status = status;
    }

    public Modelo getModelo() {
        return modelo;
    }

    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public int getQuilometragem() {
        return quilometragem;
    }

    public void setQuilometragem(int quilometragem) {
        this.quilometragem = quilometragem;
    }

    public StatusVeiculo getStatus() {
        return status;
    }

    public void setStatus(StatusVeiculo status) {
        this.status = status;
    }
}
