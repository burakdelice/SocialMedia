package com.burakdelice.rabbitmq.consumer;

import com.burakdelice.rabbitmq.model.MailModel;
import com.burakdelice.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailConsumer {

    private final MailService mailService;

    @RabbitListener(queues = "${rabbitmq.mail-queue}")
    public void  sendMail(MailModel model){
        log.info("Mail {}",model);
        mailService.sendMail(model);
    }

}
