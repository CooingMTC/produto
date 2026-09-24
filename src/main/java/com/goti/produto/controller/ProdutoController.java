package com.goti.produto.controller;

import com.goti.produto.dto.CriarProdutoDTO;
import com.goti.produto.model.Produto;
import com.goti.produto.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> criar(@RequestBody CriarProdutoDTO dto) {
        // Gera um identificador/protocolo temporário
        String protocolo = UUID.randomUUID().toString();

        CriarProdutoDTO payload = new CriarProdutoDTO(
                protocolo,
                dto.nome(),
                dto.descricao(),
                dto.imagem(),
                dto.preco(),
                dto.estoque(),
                dto.avaliacao(),
                dto.ativo());

        produtoService.solicitarCriacaoAssincrona(payload);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(Map.of(
                        "mensagem", "Solicitação de criação recebida com sucesso.",
                        "protocolo", protocolo));
    }

    @GetMapping
    public ResponseEntity<List<Produto>> listarTodos() {
        return ResponseEntity.ok(produtoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable String id) {
        return produtoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable String id, @RequestBody Produto produto) {
        return produtoService.atualizar(id, produto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        if (produtoService.deletar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}