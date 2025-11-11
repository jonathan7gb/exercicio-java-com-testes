package org.example.repository;

import org.example.model.Produto;

import java.sql.SQLException;
import java.util.List;

public interface ProdutoRepository {
    Produto save(Produto produto) throws SQLException;

    List<Produto> findAll() throws SQLException;

    Produto findById(int id) throws SQLException;

    Produto update(Produto produto) throws SQLException;

    void deleteById(int id) throws SQLException;
}
