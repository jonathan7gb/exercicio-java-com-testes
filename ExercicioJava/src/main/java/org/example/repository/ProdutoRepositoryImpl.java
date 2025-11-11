package org.example.repository;

import org.example.model.Produto;
import org.example.util.ConexaoBanco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProdutoRepositoryImpl implements ProdutoRepository{
    @Override
    public Produto save(Produto produto) throws SQLException {
        String sql = """
                INSERT INTO produto (nome, preco, quantidade, categoria)
                VALUES (?, ?, ?, ?)
                """;

        try(Connection connection = ConexaoBanco.conectar();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, produto.getNome());
            preparedStatement.setDouble(2, produto.getPreco());
            preparedStatement.setInt(3, produto.getQuantidade());
            preparedStatement.setString(4, produto.getCategoria());
            preparedStatement.execute();
        }
        return produto;
    }

    @Override
    public List<Produto> findAll() throws SQLException {
        String  sql = """
                SELECT (id, nome, preco, quantidade, categoria) 
                FROM produto;
        """;

        List<Produto> produtos = new ArrayList<>();

        try(Connection connection = ConexaoBanco.conectar();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Produto produto = new Produto();
                produto.setId(rs.getInt("id"));
                produto.setNome(rs.getString("nome"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setQuantidade(rs.getInt("quantidade"));
                produto.setCategoria(rs.getString("categoria"));
                produtos.add(produto);
            }
        }
        return produtos;
    }

    @Override
    public Produto findById(int id) throws SQLException {
        String  sql = """
                SELECT (id, nome, preco, quantidade, categoria) 
                FROM produto
                WHERE id = ?;
        """;

        Produto produto = new Produto();
        try(Connection connection = ConexaoBanco.conectar();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                produto.setId(rs.getInt("id"));
                produto.setNome(rs.getString("nome"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setQuantidade(rs.getInt("quantidade"));
                produto.setCategoria(rs.getString("categoria"));
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
            preparedStatement.setInt(5, idOriginal);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                produto.setId(rs.getInt("id"));
                produto.setNome(rs.getString("nome"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setQuantidade(rs.getInt("quantidade"));
                produto.setCategoria(rs.getString("categoria"));
            }
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
        }
    }
}
