package com.burakdelice.manager;

import com.burakdelice.dto.response.UserProfileResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import static com.burakdelice.constant.EndPoints.UPDATE;

@FeignClient(url = "http://localhost:7072/api/v1/user",decode404 = true,name = "userprofile-auth")
public interface IUserManager {

    @GetMapping("find-user-simple-data/{authId}")
    public ResponseEntity<UserProfileResponseDto> findByUserSimpleDataWithAuthId(Long authId);
}
