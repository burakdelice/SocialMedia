package com.burakdelice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RegisterRequestDto {
    @NotBlank(message = "Kullanıcı adı boş geçilemez!!!")
    private String username;
    @NotBlank(message = "Email adı boş geçilemez!!!")
    @Email
    private String email;
    @NotBlank(message = "Şifre adı boş geçilemez!!!")
    @Size(min = 5,max = 32,message = "Şifre uzunluğu en az 5 karakter en fazla 32 karakter olabilir!!")
   //@Pattern(regexp = "^.*(?=.{5,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$")
    private String password;
}
