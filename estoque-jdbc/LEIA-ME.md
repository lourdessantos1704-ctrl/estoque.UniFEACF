# Estoque de Veículos – versão JDBC (estilo da aula)

Java puro + MySQL Connector. Programa de console com menu (CRUD de marcas, modelos e veículos + filtros).
O próprio programa cria o banco `estoque_veiculos` e as tabelas na primeira execução.

## Como rodar (IntelliJ)
1. Abra o XAMPP e clique **Start** no MySQL.
2. IntelliJ: **File > Open** e escolha a pasta `estoque-jdbc` (ele reconhece o `pom.xml` e baixa o MySQL Connector sozinho).
3. Se aparecer "Load Maven Changes", clique. Se pedir SDK: **Download JDK** (17 ou mais novo).
4. Abra `src/main/java/br/com/fecaf/App.java` e clique no triângulo verde.
5. No console aparece o menu. Cadastre marcas, modelos e veiculos pelas opcoes.

Usuário/senha do MySQL: classe `database/Conexao.java` (padrão: root, sem senha, que é o do XAMPP).

## Estrutura
- `database/Conexao.java`  conexão JDBC + criação do banco e tabelas
- `model/`  EntidadeBase (herança), Marca, Modelo, Veiculo, StatusVeiculo
- `controller/`  MarcaController, ModeloController, VeiculoController (SQL com PreparedStatement)
- `App.java`  menu
- `sql/model.sql`  modelo físico do MySQL
