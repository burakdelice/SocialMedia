package com.burakdelice.rabbitmq.consumer;

import com.burakdelice.rabbitmq.model.RegisterModel;
import com.burakdelice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterConsumer {

    private final UserService userService;

    @RabbitListener(queues = "${rabbitmq.register-queue}")
    public void newUserCreta(RegisterModel model){
        System.out.println(model);
        log.info("User {}", model);
        userService.createNewUserWithRabbitmq(model);
    }
}
