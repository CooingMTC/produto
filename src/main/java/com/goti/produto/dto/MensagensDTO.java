package com.goti.produto.dto;

import java.io.Serializable;

public class MensagensDTO {

    // Mensagem recebida da fila vinda do serviço 'venda'
    public record MensagemSolicitacaoDTO(
            String protocolo,
            String liveId,
            Integer quantidade
    ) implements Serializable {}

    // Mensagem enviada de volta para o serviço 'venda'
    public record MensagemRespostaDTO(
            String protocolo,
            boolean aprovado,
            String motivo
    ) implements Serializable {}
}