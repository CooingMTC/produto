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

    public static final String VENDA_EXCHANGE = "venda.exchange";
    public static final String FILA_SOLICITACAO = "venda.solicitacao.fila";
    public static final String ROUTING_KEY_SOLICITACAO = "venda.comando.solicitar";

    public static final String FILA_RESPOSTA = "venda.resposta.fila";
    public static final String ROUTING_KEY_RESPOSTA = "venda.evento.processado";

    @Bean
    public DirectExchange vendaExchange() {
        return new DirectExchange(VENDA_EXCHANGE);
    }

    @Bean
    public Queue filaSolicitacao() {
        return QueueBuilder.durable(FILA_SOLICITACAO).build();
    }

    @Bean
    public Queue filaResposta() {
        return QueueBuilder.durable(FILA_RESPOSTA).build();
    }

    @Bean
    public Binding bindingSolicitacao(Queue filaSolicitacao, DirectExchange vendaExchange) {
        return BindingBuilder.bind(filaSolicitacao).to(vendaExchange).with(ROUTING_KEY_SOLICITACAO);
    }

    @Bean
    public Binding bindingResposta(Queue filaResposta, DirectExchange vendaExchange) {
        return BindingBuilder.bind(filaResposta).to(vendaExchange).with(ROUTING_KEY_RESPOSTA);
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

    // Mantém o pool de concorrência com 3 a 10 workers
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        factory.setPrefetchCount(1);
        return factory;
    }
}