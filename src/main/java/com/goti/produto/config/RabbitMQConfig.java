package com.goti.produto.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PRODUTO_EXCHANGE = "produto.exchange";
    public static final String FILA_CRIACAO_PRODUTO = "produto.criacao.fila";
    public static final String ROUTING_KEY_CRIACAO = "produto.comando.criar";

    @Bean
    public DirectExchange produtoExchange() {
        return new DirectExchange(PRODUTO_EXCHANGE);
    }

    @Bean
    public Queue filaCriacaoProduto() {
        // durable = true garante persistência mesmo se o RabbitMQ reiniciar
        return QueueBuilder.durable(FILA_CRIACAO_PRODUTO).build();
    }

    @Bean
    public Binding bindingCriacao(Queue filaCriacaoProduto, DirectExchange produtoExchange) {
        return BindingBuilder.bind(filaCriacaoProduto).to(produtoExchange).with(ROUTING_KEY_CRIACAO);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

    // Configuração de concorrência do consumidor
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter) {
        
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        
        // CENÁRIO DE CONCORRÊNCIA:
        // Define 3 threads iniciais e no máximo 10 processando a fila ao mesmo tempo
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        factory.setPrefetchCount(1); // Garante distribuição justa entre workers
        
        return factory;
    }
}