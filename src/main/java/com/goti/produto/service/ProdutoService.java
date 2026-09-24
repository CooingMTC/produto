package com.goti.produto.service;

import com.goti.produto.config.RabbitMQConfig;
import com.goti.produto.dto.CriarProdutoDTO;
import com.goti.produto.model.Produto;
import com.goti.produto.producer.ProdutoProducer;
import com.goti.produto.repository.ProdutoRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoProducer produtoProducer;

    public ProdutoService(ProdutoRepository produtoRepository, ProdutoProducer produtoProducer) {
        this.produtoRepository = produtoRepository;
        this.produtoProducer = produtoProducer;
    }

    // Chamado pelo Controller: fluxo não-bloqueante
    public void solicitarCriacaoAssincrona(CriarProdutoDTO dto) {
        produtoProducer.enviarSolicitacaoCriacao(dto);
    }

    // Consumidor da fila com concorrência gerenciada pelo RabbitMQ
    @RabbitListener(
        queues = RabbitMQConfig.FILA_CRIACAO_PRODUTO, 
        containerFactory = "rabbitListenerContainerFactory"
    )
    @Transactional
    public void processarCriacaoProduto(CriarProdutoDTO dto) {
        Produto produto = new Produto();
        produto.setNome(dto.nome());
        produto.setDescricao(dto.descricao());
        produto.setImagem(dto.imagem());
        produto.setPreco(dto.preco());
        produto.setEstoque(dto.estoque());
        produto.setAvaliacao(dto.avaliacao());
        produto.setAtivo(dto.ativo());

        // Salva com segurança; o prefetchCount e pool de consumers protegem contra picos de concorrência
        produtoRepository.save(produto);
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