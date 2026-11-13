package br.com.fecaf;

import br.com.fecaf.controller.MarcaController;
import br.com.fecaf.controller.ModeloController;
import br.com.fecaf.controller.VeiculoController;
import br.com.fecaf.database.Conexao;
import br.com.fecaf.model.Marca;
import br.com.fecaf.model.Modelo;
import br.com.fecaf.model.StatusVeiculo;
import br.com.fecaf.model.Veiculo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class App {
    private static final Scanner sc = new Scanner(System.in);
    private static final Locale BR = Locale.forLanguageTag("pt-BR");

    private static MarcaController marcas;
    private static ModeloController modelos;
    private static VeiculoController veiculos;

    public static void main(String[] args) {
        Conexao conexao = new Conexao();
        try {
            conexao.criarBancoETabelas();
            Connection c = conexao.conectar();
            marcas = new MarcaController(c);
            modelos = new ModeloController(c);
            veiculos = new VeiculoController(c);
        } catch (SQLException e) {
            System.out.println("Nao foi possivel conectar. Coloque o usuario e a senha do seu MySQL na classe Conexao.");
            return;
        }

        int opcao;
        do {
            mostrarMenu();
            opcao = lerInteiro("Escolha uma opcao: ", true);
            try {
                switch (opcao) {
                    case 1 -> cadastrarMarca();
                    case 2 -> listarMarcas();
                    case 3 -> cadastrarModelo();
                    case 4 -> listarModelos();
                    case 5 -> cadastrarVeiculo();
                    case 6 -> listarVeiculos();
                    case 7 -> filtrarVeiculos();
                    case 8 -> atualizarVeiculo();
                    case 9 -> removerVeiculo();
                    case 10 -> removerModelo();
                    case 0 -> System.out.println("Ate logo!");
                    default -> System.out.println("Opcao invalida.");
                }
            } catch (SQLException e) {
                System.out.println("ERRO no banco: " + e.getMessage());
            }
            if (opcao != 0) {
                pausar();
            }
        } while (opcao != 0);
    }

    // ================= MENU =================

    private static void mostrarMenu() {
        limparTela();
        System.out.println("===== ESTOQUE DE VEICULOS =====");
        System.out.println("1  - Cadastrar marca");
        System.out.println("2  - Listar marcas");
        System.out.println("3  - Cadastrar modelo");
        System.out.println("4  - Listar modelos");
        System.out.println("5  - Cadastrar veiculo");
        System.out.println("6  - Listar todos os veiculos");
        System.out.println("7  - Filtrar veiculos");
        System.out.println("8  - Atualizar veiculo (preco, km, status)");
        System.out.println("9  - Remover veiculo");
        System.out.println("10 - Remover modelo");
        System.out.println("0  - Sair");
    }

    // ================= MARCAS =================

    private static void cadastrarMarca() throws SQLException {
        String nome = lerTexto("Nome da marca: ");
        if (nome.isEmpty()) {
            System.out.println("O nome e obrigatorio.");
            return;
        }
        Marca marca = new Marca(nome);
        marcas.inserir(marca);
        System.out.println("Marca cadastrada com id " + marca.getId());
    }

    private static void listarMarcas() throws SQLException {
        List<Marca> lista = marcas.listar();
        if (lista.isEmpty()) {
            System.out.println("Nenhuma marca cadastrada.");
            return;
        }
        System.out.println("--- MARCAS ---");
        for (Marca m : lista) {
            System.out.println(m.getId() + " - " + m.getNome());
        }
    }

    // ================= MODELOS =================

    private static void cadastrarModelo() throws SQLException {
        listarMarcas();
        Integer marcaId = lerInteiro("Id da marca: ", true);
        if (!marcas.existe(marcaId)) {
            System.out.println("Marca nao encontrada.");
            return;
        }
        String nome = lerTexto("Nome do modelo: ");
        if (nome.isEmpty()) {
            System.out.println("O nome e obrigatorio.");
            return;
        }
        Marca marca = new Marca();
        marca.setId(marcaId.longValue());
        Modelo modelo = new Modelo(nome, marca);
        modelos.inserir(modelo);
        System.out.println("Modelo cadastrado com id " + modelo.getId());
    }

    private static void listarModelos() throws SQLException {
        List<Modelo> lista = modelos.listar();
        if (lista.isEmpty()) {
            System.out.println("Nenhum modelo cadastrado.");
            return;
        }
        System.out.println("--- MODELOS ---");
        for (Modelo m : lista) {
            System.out.println(m.getId() + " - " + m.getMarca().getNome() + " " + m.getNome());
        }
    }

    private static void removerModelo() throws SQLException {
        listarModelos();
        Integer id = lerInteiro("Id do modelo a remover: ", true);
        if (!modelos.existe(id)) {
            System.out.println("Modelo nao encontrado.");
            return;
        }
        if (modelos.temVeiculos(id)) {
            System.out.println("Nao e possivel remover: existem veiculos com esse modelo.");
            return;
        }
        modelos.remover(id);
        System.out.println("Modelo removido.");
    }

    // ================= VEICULOS =================

    private static void cadastrarVeiculo() throws SQLException {
        listarModelos();
        Integer modeloId = lerInteiro("Id do modelo: ", true);
        if (!modelos.existe(modeloId)) {
            System.out.println("Modelo nao encontrado.");
            return;
        }
        Integer ano = lerInteiro("Ano de fabricacao: ", true);
        if (ano < 1900 || ano > 2100) {
            System.out.println("Ano invalido (1900 a 2100).");
            return;
        }
        String cor = lerTexto("Cor: ");
        if (cor.isEmpty()) {
            System.out.println("A cor e obrigatoria.");
            return;
        }
        BigDecimal preco = lerDecimal("Preco: ", true);
        if (preco.signum() < 0) {
            System.out.println("O preco nao pode ser negativo.");
            return;
        }
        Integer km = lerInteiro("Quilometragem: ", true);
        if (km < 0) {
            System.out.println("A quilometragem nao pode ser negativa.");
            return;
        }
        StatusVeiculo status = lerStatus("Status", true);

        Modelo modelo = new Modelo();
        modelo.setId(modeloId.longValue());
        Veiculo v = new Veiculo(modelo, ano, cor, preco, km, status);
        veiculos.inserir(v);
        System.out.println("Veiculo cadastrado com id " + v.getId());
    }

    private static void listarVeiculos() throws SQLException {
        mostrarVeiculos(veiculos.listarTodos());
    }

    private static void filtrarVeiculos() throws SQLException {
        System.out.println("Preencha so os filtros que quiser (ENTER = ignorar).");
        Integer marcaId = lerInteiro("Id da marca: ", false);
        Integer modeloId = lerInteiro("Id do modelo: ", false);
        BigDecimal precoMin = lerDecimal("Preco minimo: ", false);
        BigDecimal precoMax = lerDecimal("Preco maximo: ", false);
        Integer ano = lerInteiro("Ano: ", false);
        StatusVeiculo status = lerStatus("Status", false);

        List<Veiculo> lista = veiculos.listar(
                marcaId == null ? null : marcaId.longValue(),
                modeloId == null ? null : modeloId.longValue(),
                precoMin, precoMax, ano, status);
        mostrarVeiculos(lista);
    }

    private static void atualizarVeiculo() throws SQLException {
        Integer id = lerInteiro("Id do veiculo: ", true);
        Veiculo v = veiculos.buscarPorId(id);
        if (v == null) {
            System.out.println("Veiculo nao encontrado.");
            return;
        }
        System.out.println("Atual: " + formatar(v));
        System.out.println("(ENTER mantem o valor atual)");

        BigDecimal preco = lerDecimal("Novo preco: ", false);
        if (preco != null) {
            if (preco.signum() < 0) {
                System.out.println("O preco nao pode ser negativo.");
                return;
            }
            v.setPreco(preco);
        }
        Integer km = lerInteiro("Nova quilometragem: ", false);
        if (km != null) {
            if (km < 0) {
                System.out.println("A quilometragem nao pode ser negativa.");
                return;
            }
            v.setQuilometragem(km);
        }
        StatusVeiculo status = lerStatus("Novo status", false);
        if (status != null) {
            v.setStatus(status);
        }

        veiculos.atualizar(v);
        System.out.println("Veiculo atualizado: " + formatar(veiculos.buscarPorId(id)));
    }

    private static void removerVeiculo() throws SQLException {
        Integer id = lerInteiro("Id do veiculo a remover: ", true);
        if (veiculos.remover(id)) {
            System.out.println("Veiculo removido.");
        } else {
            System.out.println("Veiculo nao encontrado.");
        }
    }

    // ================= EXIBICAO =================

    private static void mostrarVeiculos(List<Veiculo> lista) {
        if (lista.isEmpty()) {
            System.out.println("Nenhum veiculo encontrado.");
            return;
        }
        System.out.println("--- VEICULOS (" + lista.size() + ") ---");
        for (Veiculo v : lista) {
            System.out.println(formatar(v));
        }
    }

    private static String formatar(Veiculo v) {
        return String.format(BR, "#%d | %s %s | %d | %s | R$ %,.2f | %,d km | %s",
                v.getId(), v.getModelo().getMarca().getNome(), v.getModelo().getNome(),
                v.getAno(), v.getCor(), v.getPreco(), v.getQuilometragem(), v.getStatus());
    }

    // ================= LEITURA DO TECLADO =================

    private static void limparTela() {
        System.out.println("\n".repeat(50));
    }

    private static void pausar() {
        System.out.println();
        lerTexto("Pressione ENTER para voltar ao menu...");
    }

    private static String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return sc.nextLine().trim();
    }

    private static Integer lerInteiro(String mensagem, boolean obrigatorio) {
        while (true) {
            String texto = lerTexto(mensagem);
            if (texto.isEmpty() && !obrigatorio) {
                return null;
            }
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Digite um numero inteiro valido.");
            }
        }
    }

    private static BigDecimal lerDecimal(String mensagem, boolean obrigatorio) {
        while (true) {
            String texto = lerTexto(mensagem);
            if (texto.isEmpty() && !obrigatorio) {
                return null;
            }
            try {
                return new BigDecimal(texto.replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("Digite um valor valido.");
            }
        }
    }

    private static StatusVeiculo lerStatus(String mensagem, boolean obrigatorio) {
        while (true) {
            String texto = lerTexto(mensagem + " (1-DISPONIVEL 2-RESERVADO 3-VENDIDO 4-DESCONTINUADO): ");
            if (texto.isEmpty() && !obrigatorio) {
                return null;
            }
            switch (texto) {
                case "1": return StatusVeiculo.DISPONIVEL;
                case "2": return StatusVeiculo.RESERVADO;
                case "3": return StatusVeiculo.VENDIDO;
                case "4": return StatusVeiculo.DESCONTINUADO;
                default: System.out.println("Digite 1, 2, 3 ou 4.");
            }
        }
    }
}
