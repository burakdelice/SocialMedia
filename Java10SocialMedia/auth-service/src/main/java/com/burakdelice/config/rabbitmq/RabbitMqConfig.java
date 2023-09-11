package com.burakdelice.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
public class RabbitMqConfig {

    @Value("${rabbitmq.auth-exchange}")
    private String exchane;
    @Value("${rabbitmq.register-binding-key}")
    private String registerBindingKey;
    @Value("${rabbitmq.activation-binding-key}")
    private String activationBindingKey;
    @Value("${rabbitmq.register-queue}")
    private String registerQueueName;
    @Value("${rabbitmq.activation-queue}")
    private String activationQueueName;
    @Value("${rabbitmq.mail-queue}")
    private String mailQueueName;
    @Value("${rabbitmq.mail-binding-key}")
    private String mailBindingKey;

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(exchane);
    }

    @Bean
    Queue reqisterQueue() {
        return new Queue(registerQueueName);
    }

    @Bean
    Queue activationQueue() {
        return new Queue(activationQueueName);
    }
    @Bean
    Queue mailQueue() {
        return new Queue(mailQueueName);
    }
    @Primary
    public Binding bindingRegister(final Queue registerQueue, final DirectExchange exchange) {
        return BindingBuilder.bind(registerQueue).to(exchange).with(registerBindingKey);
    }
    @Bean
    public Binding bindingActivation(final Queue activationQueue, final DirectExchange exchange) {
        return BindingBuilder.bind(activationQueue).to(exchange).with(activationBindingKey);
    }
    @Bean
    public Binding bindingMail(final Queue mailQueue, final DirectExchange exchange) {
        return BindingBuilder.bind(mailQueue).to(exchange).with(mailBindingKey);
    }
}
