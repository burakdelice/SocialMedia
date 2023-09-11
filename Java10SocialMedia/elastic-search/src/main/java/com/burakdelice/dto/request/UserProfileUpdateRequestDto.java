package com.burakdelice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileUpdateRequestDto {

    private String token;
    @NotBlank(message = "Kullanıcı adı bos geçilemez")
    private String username;
    @Email
    private String email;
    private String phone;
    private String address;
    private String avatar;
    private String about;
    private String name;
    private String surName;

}
