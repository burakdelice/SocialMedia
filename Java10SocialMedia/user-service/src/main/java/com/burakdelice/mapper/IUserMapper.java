package com.burakdelice.mapper;

import com.burakdelice.dto.request.AuthUpdateRequestDto;
import com.burakdelice.dto.request.UserSaveRequestDto;
import com.burakdelice.dto.request.UserUpdateRequestDto;
import com.burakdelice.dto.response.UserProfileFindAllResponseDto;
import com.burakdelice.rabbitmq.model.RegisterElasticModel;
import com.burakdelice.rabbitmq.model.RegisterModel;
import com.burakdelice.repository.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IUserMapper {

    IUserMapper INSTANCE = Mappers.getMapper(IUserMapper.class);

    UserProfile toUserProfile(UserSaveRequestDto dto);

    UserProfile toUserProfile(UserUpdateRequestDto dto);

    UserProfile toUserProfile(RegisterModel model);

    @Mapping(source = "authId",target = "id")
    AuthUpdateRequestDto toAuthUpdateRequestDto(UserProfile userProfile);

  //  @Mapping(source = "id" , target = "userProfileId")
    UserProfileFindAllResponseDto toUserProfileFindAllResponseDto(UserProfile userProfile);

    RegisterElasticModel toRegisterElasticModel(UserProfile userProfile);
}
