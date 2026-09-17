package com.goti.produto.service;

import com.goti.produto.model.Produto;
import com.goti.produto.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public Produto criar(Produto produto) {
        produto.setId(null); // Garante que o JPA gere o UUID
        return produtoRepository.save(produto);
    }

    @Transactional(readOnly = true)
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Produto> buscarPorId(String id) {
        return produtoRepository.findById(id);
    }

    @Transactional
    public Optional<Produto> atualizar(String id, Produto dadosAtualizados) {
        return produtoRepository.findById(id).map(produtoExistente -> {
            produtoExistente.setNome(dadosAtualizados.getNome());
            produtoExistente.setDescricao(dadosAtualizados.getDescricao());
            produtoExistente.setImagem(dadosAtualizados.getImagem());
            produtoExistente.setPreco(dadosAtualizados.getPreco());
            produtoExistente.setEstoque(dadosAtualizados.getEstoque());
            produtoExistente.setAvaliacao(dadosAtualizados.getAvaliacao());
            produtoExistente.setAtivo(dadosAtualizados.getAtivo());
            return produtoRepository.save(produtoExistente);
        });
    }

    @Transactional
    public boolean deletar(String id) {
        if (produtoRepository.existsById(id)) {
            produtoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}