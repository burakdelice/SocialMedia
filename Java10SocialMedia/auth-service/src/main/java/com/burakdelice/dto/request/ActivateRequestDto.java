package com.burakdelice.dto.request;

import com.auth0.jwt.JWT;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ActivateRequestDto {

    private String token;
    private String activationCode;

}
