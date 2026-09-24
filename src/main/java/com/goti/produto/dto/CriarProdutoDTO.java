package com.goti.produto.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public record CriarProdutoDTO(
    String idTemporario,
    String nome,
    String descricao,
    List<String> imagem,
    BigDecimal preco,
    Integer estoque,
    Double avaliacao,
    Boolean ativo
) implements Serializable {}