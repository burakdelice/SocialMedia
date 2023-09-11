package com.burakdelice.config.rabbitmq;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {


    private String registerQueueName= "register-queue";
  //  @Value("${rabbitmq.activation-queue}")
    private String activationQueueName="queue-activation";
  //  @Value("${rabbitmq.register-elastic-queue}")
    private String registerElasticQueueName="register-elastic-queue";
  //  @Value("${rabbitmq.register-elastic-binding}")
    private String registerElasticBinding = "register-elastic-binding";
  //  @Value("${rabbitmq.user-exchange}")
    private String exchange = "exchange-user";

    @Bean
    Queue registerQueue(){
        return new Queue(registerQueueName);
    }

    @Bean
    Queue activationQueue() {
        return new Queue(activationQueueName);
    }
    @Bean
    Queue registerElasticQueue(){
        return new Queue(registerElasticQueueName);
    }
    @Bean
    public DirectExchange userExchange(){
        return new DirectExchange(exchange);
    }

    @Bean
    public Binding bindingRegisterElastic(final Queue registerElasticQueue, final DirectExchange userExchange){
        return BindingBuilder.bind(registerElasticQueue).to(userExchange).with(registerElasticBinding);
    }
}
