package com.burakdelice.utility;

import com.burakdelice.dto.response.UserProfileFindAllResponseDto;
import com.burakdelice.manager.IUserManager;
import com.burakdelice.mapper.IElasticMapper;
import com.burakdelice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllData {

private final IUserManager userManager;

private final UserService userService;

//@PostConstruct
public void initData(){
   List<UserProfileFindAllResponseDto> list=userManager.findAll().getBody();
    //List<UserProfile>
   userService.saveAll(IElasticMapper.INSTANCE.toUserProfiles(list));
}

}
