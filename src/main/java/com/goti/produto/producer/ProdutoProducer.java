package com.goti.produto.producer;

import com.goti.produto.config.RabbitMQConfig;
import com.goti.produto.dto.CriarProdutoDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProdutoProducer {

    private final RabbitTemplate rabbitTemplate;

    public ProdutoProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarSolicitacaoCriacao(CriarProdutoDTO dto) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.PRODUTO_EXCHANGE,
            RabbitMQConfig.ROUTING_KEY_CRIACAO,
            dto
        );
    }
}