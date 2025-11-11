package org.example.service;

import org.example.model.Produto;
import org.example.repository.ProdutoRepositoryImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProdutoServiceImpl implements ProdutoService{
    ProdutoRepositoryImpl produtoRepository = new ProdutoRepositoryImpl();

    @Override
    public Produto cadastrarProduto(Produto produto){
       Produto produtoCadastrado = null;
       try{
           if(produto.getPreco() < 0){
              throw new IllegalArgumentException("Preço deve ser positivo.");
           }else{
               produtoCadastrado = produtoRepository.save(produto);
           }
       }catch (SQLException e){
           e.printStackTrace();
       }
       return produtoCadastrado;
    }

    @Override
    public List<Produto> listarProdutos(){
        List<Produto> produtos = new ArrayList<>();
        try{
            produtos = produtoRepository.findAll();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return produtos;
    }

    @Override
    public Produto buscarPorId(int id){
        Produto produto = null;
        try{
            produto = produtoRepository.findById(id);
            if (produto == null){
                return null;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return produto;
    }

    @Override
    public Produto atualizarProduto(Produto produto, int id){
        Produto produtoAtualizado = null;
        try{
            Produto p = produtoRepository.findById(id);
            if (p == null){
                return null;
            }else{
                produtoAtualizado = produtoRepository.update(produto, id);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return produtoAtualizado;
    }

    @Override
    public boolean excluirProduto(int id){
        try{
            Produto produto = produtoRepository.findById(id);
            if (produto == null){
                return false;
            }else{
                produtoRepository.deleteById(id);
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
