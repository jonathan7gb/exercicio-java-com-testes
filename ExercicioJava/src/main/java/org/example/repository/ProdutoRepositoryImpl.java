package org.example.repository;

import org.example.model.Produto;
import org.example.util.ConexaoBanco;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoRepositoryImpl implements ProdutoRepository{
    @Override
    public Produto save(Produto produto) throws SQLException {
        String sql = """
                INSERT INTO produto (nome, preco, quantidade, categoria)
                VALUES (?, ?, ?, ?)
                """;

        int id = 0;
        try(Connection connection = ConexaoBanco.conectar();
        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, produto.getNome());
            preparedStatement.setDouble(2, produto.getPreco());
            preparedStatement.setInt(3, produto.getQuantidade());
            preparedStatement.setString(4, produto.getCategoria());
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getInt(1);
                        produto.setId(id);
                    }
                }
            }

        }
        return produto;
    }

    @Override
    public List<Produto> findAll() throws SQLException {
        String  sql = """
                SELECT (nome)
                FROM produto;
        """;

        List<Produto> produtos = new ArrayList<>();

        try(Connection connection = ConexaoBanco.conectar();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Produto produto = new Produto();
                produto.setNome(rs.getString("nome"));
                produtos.add(produto);
            }
        }
        return produtos;
    }

    @Override
    public Produto findById(int id) throws SQLException {
        String  sql = """
                SELECT (nome)
                FROM produto
                WHERE id = ?;
        """;

        Produto produto = null;
        try(Connection connection = ConexaoBanco.conectar();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                produto = new Produto();
                produto.setNome(rs.getString("nome"));
            }
        }
        return produto;
    }

    @Override
    public Produto update(Produto produto, int idOriginal) throws SQLException {
        String  sql = """
                UPDATE produto
                SET nome = ?,
                preco = ?,
                quantidade = ?,
                categoria = ?
                WHERE id = ?;
        """;

        try(Connection connection = ConexaoBanco.conectar();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1, produto.getNome());
            preparedStatement.setDouble(2, produto.getPreco());
            preparedStatement.setInt(3, produto.getQuantidade());
            preparedStatement.setString(4, produto.getCategoria());
            preparedStatement.setInt(5, idOriginal);
            preparedStatement.executeUpdate();
        }
        return produto;
    }

    @Override
    public void deleteById(int id) throws SQLException {
        String  sql = """
                DELETE FROM produto
                WHERE id = ?;
        """;

        try(Connection connection = ConexaoBanco.conectar();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }
}
