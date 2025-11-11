import org.example.model.Produto;
import org.example.service.ProdutoService;
import org.example.service.ProdutoServiceImpl;
import org.example.util.ConexaoBanco;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Teste de Integração - ProdutoService com Banco Real (Teste)")
public class ProdutoServiceIntegrationTest {

    private ProdutoService produtoService;

    // SQL para criar a tabela (fornecido por você)
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE produto (" +
                    "   id INT AUTO_INCREMENT PRIMARY KEY," +
                    "   nome VARCHAR(100) NOT NULL," +
                    "   preco DOUBLE NOT NULL," +
                    "   quantidade INT NOT NULL," +
                    "   categoria VARCHAR(50) NOT NULL" +
                    ");";

    // SQL para destruir a tabela
    private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS produto;";

    // SQL para limpar a tabela (TRUNCATE reseta o AUTO_INCREMENT)
    private static final String SQL_TRUNCATE_TABLE = "TRUNCATE TABLE produto;";
    // (Alternativa se TRUNCATE não for permitido: "DELETE FROM produto;")

    @BeforeAll
    static void setupDatabase() throws Exception {
        // 1. Conecta ao banco de TESTE
        try (Connection conn = ConexaoBanco.conectar();
             Statement stmt = conn.createStatement()) {

            // 2. Destrói a tabela (caso exista de um teste anterior falho)
            stmt.execute(SQL_DROP_TABLE);

            // 3. Cria a tabela
            stmt.execute(SQL_CREATE_TABLE);

            System.out.println("Tabela 'produto' criada no banco de teste.");

        } catch (Exception e) {
            System.err.println("Erro ao configurar o banco de teste (BeforeAll)");
            e.printStackTrace();
            throw e; // Falha o setup se não conseguir criar a tabela
        }
    }

    @AfterAll
    static void tearDownDatabase() throws Exception {
        // 4. Destrói a tabela ao final de TODOS os testes
        try (Connection conn = ConexaoBanco.conectar();
             Statement stmt = conn.createStatement()) {

            stmt.execute(SQL_DROP_TABLE);
            System.out.println("Tabela 'produto' destruída.");

        } catch (Exception e) {
            System.err.println("Erro ao limpar o banco de teste (AfterAll)");
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setupTest() throws Exception {
        // 5. Limpa os dados da tabela ANTES de cada teste
        try (Connection conn = ConexaoBanco.conectar();
             Statement stmt = conn.createStatement()) {

            stmt.execute(SQL_TRUNCATE_TABLE);

        } catch (Exception e) {
            System.err.println("Erro ao limpar a tabela (BeforeEach)");
            e.printStackTrace();
        }

        // 6. Instancia o Service
        // Isso fará com que o Service crie seu Repositório,
        // que por sua vez usará ConexaoBanco.conectar()
        produtoService = new ProdutoServiceImpl();
    }

    @Test
    @DisplayName("Deve cadastrar um produto e salvá-lo no banco")
    void testCadastrarProduto_Sucesso() throws Exception {
        // ARRANGE
        Produto p = new Produto("Monitor 4K", 1800.00, 10, "Eletrônicos");

        // ACT
        Produto produtoSalvo = produtoService.cadastrarProduto(p);

        // ASSERT (Service)
        assertNotNull(produtoSalvo);
        assertTrue(produtoSalvo.getId() > 0, "ID não foi gerado pelo banco");
        assertEquals("Monitor 4K", produtoSalvo.getNome());

        // ASSERT (Verificação direta no Banco)
        try (Connection conn = ConexaoBanco.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM produto WHERE id = " + produtoSalvo.getId())) {

            assertTrue(rs.next(), "Produto não foi salvo no banco");
            assertEquals("Monitor 4K", rs.getString("nome"));
            assertEquals(1800.00, rs.getDouble("preco"));
            assertEquals("Eletrônicos", rs.getString("categoria"));
        }
    }

    @Test
    @DisplayName("Não deve cadastrar produto com preço negativo (Regra de Negócio)")
    void testCadastrarProduto_PrecoNegativo() {
        // ARRANGE
        Produto p = new Produto("Mouse", -50.00, 5, "Periféricos");

        // ACT & ASSERT
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            produtoService.cadastrarProduto(p);
        }, "Serviço não validou preço negativo");

        assertEquals("Preço deve ser positivo.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve listar todos os produtos cadastrados")
    void testListarProdutos() throws SQLException {
        // ARRANGE
        produtoService.cadastrarProduto(new Produto("Teclado", 150.00, 20, "Periféricos"));
        produtoService.cadastrarProduto(new Produto("Webcam", 400.00, 5, "Eletrônicos"));

        // ACT
        List<Produto> produtos = produtoService.listarProdutos();

        // ASSERT
        assertNotNull(produtos);
        assertEquals(2, produtos.size());
    }

    @Test
    @DisplayName("Deve atualizar um produto existente no banco")
    void testAtualizarProduto_Sucesso() throws Exception {
        // ARRANGE
        Produto produtoOriginal = produtoService.cadastrarProduto(new Produto("Gabinete", 300.00, 5, "Hardware"));
        int idOriginal = produtoOriginal.getId();

        Produto dadosAtualizados = new Produto("Gabinete ATX", 350.00, 4, "Hardware");

        // ACT
        produtoService.atualizarProduto(dadosAtualizados, idOriginal);

        // ASSERT (Verificação direta no Banco)
        try (Connection conn = ConexaoBanco.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM produto WHERE id = " + idOriginal)) {

            assertTrue(rs.next());
            assertEquals("Gabinete ATX", rs.getString("nome"));
            assertEquals(350.00, rs.getDouble("preco"));
            assertEquals(4, rs.getInt("quantidade"));
        }
    }

    @Test
    @DisplayName("Deve excluir um produto e removê-lo do banco")
    void testExcluirProduto_Sucesso() throws Exception {
        // ARRANGE
        Produto p = produtoService.cadastrarProduto(new Produto("Cadeira", 800.00, 3, "Móveis"));
        int idParaExcluir = p.getId();

        // ACT
        boolean resultado = produtoService.excluirProduto(idParaExcluir);

        // ASSERT (Service)
        assertTrue(resultado, "Método de exclusão deveria retornar true");

        // ASSERT (Banco)
        Produto produtoAposExclusao = produtoService.buscarPorId(idParaExcluir);
        assertNull(produtoAposExclusao, "Produto não foi removido do banco");
    }

    @Test
    @DisplayName("Deve retornar false ao tentar excluir ID inexistente")
    void testExcluirProduto_NaoEncontrado() throws SQLException{
        // ACT
        boolean resultado = produtoService.excluirProduto(999);

        // ASSERT
        assertFalse(resultado, "Método de exclusão deveria retornar false para ID inexistente");
    }
}