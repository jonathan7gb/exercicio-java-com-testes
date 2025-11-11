package org.example.service;

import org.example.model.Produto;

import java.sql.SQLException;
import java.util.List;

public interface ProdutoService {
    Produto cadastrarProduto(Produto produto) throws SQLException;

    List<Produto> listarProdutos() throws SQLException;

    Produto buscarPorId(int id) throws SQLException;

    Produto atualizarProduto(Produto produto, int id) throws SQLException;

    boolean excluirProduto(int id) throws SQLException;
}
