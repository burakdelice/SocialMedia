package com.burakdelice.rabbitmq.producer;

import com.burakdelice.rabbitmq.model.ActivationModel;
import com.burakdelice.rabbitmq.model.RegisterModel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivationProducer {

    private final RabbitTemplate rabbitTemplate;
    @Value("${rabbitmq.auth-exchange}")
    private String exchange;
    @Value("${rabbitmq.activation-binding-key}")
    private String bindingKey;

    public void activateUser(ActivationModel model){
        rabbitTemplate.convertAndSend(exchange,bindingKey,model);
    }
}
