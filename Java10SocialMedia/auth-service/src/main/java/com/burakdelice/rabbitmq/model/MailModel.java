package com.burakdelice.rabbitmq.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailModel implements Serializable {
    private String email;
    private String activationCode;
    private String token;
    private String username; //gerekli değil
}
