package org.example.service;

import org.example.model.Produto;

import java.sql.SQLException;
import java.util.List;

public interface ProdutoService {
    Produto cadastrarProduto(Produto produto);

    List<Produto> listarProdutos();

    Produto buscarPorId(int id);

    Produto atualizarProduto(Produto produto, int id);

    boolean excluirProduto(int id);
}
