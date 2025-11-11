package org.example.service;

import org.example.model.Produto;
import org.example.repository.ProdutoRepositoryImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProdutoServiceImpl implements ProdutoService{
    ProdutoRepositoryImpl produtoRepository = new ProdutoRepositoryImpl();

    @Override
    public Produto cadastrarProduto(Produto produto) throws SQLException {
       Produto produtoCadastrado = null;
        try{
            produtoCadastrado = produtoRepository.save(produto);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return produtoCadastrado;
    }

    @Override
    public List<Produto> listarProdutos() throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        try{
            produtos = produtoRepository.findAll();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return produtos;
    }

    @Override
    public Produto buscarPorId(int id) throws SQLException {
        Produto produto = null;
        try{
            produto = produtoRepository.findById(id);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return produto;
    }

    @Override
    public Produto atualizarProduto(Produto produto, int id) throws SQLException {
        Produto produtoAtualizado = null;
        try{
            produtoAtualizado = produtoRepository.update(produto, id);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return produtoAtualizado;
    }

    @Override
    public boolean excluirProduto(int id) throws SQLException {
        try{
            produtoRepository.deleteById(id);
            return true;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
