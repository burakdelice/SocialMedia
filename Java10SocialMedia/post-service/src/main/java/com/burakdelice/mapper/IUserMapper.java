package com.burakdelice.mapper;

import com.burakdelice.dto.response.UserProfileFindAllResponseDto;
import com.burakdelice.repository.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IUserMapper {

    IUserMapper INSTANCE = Mappers.getMapper(IUserMapper.class);

    Post toUserProfile(UserSaveRequestDto dto);

    Post toUserProfile(UserUpdateRequestDto dto);

    Post toUserProfile(RegisterModel model);

    @Mapping(source = "authId",target = "id")
    AuthUpdateRequestDto toAuthUpdateRequestDto(Post userProfile);

  //  @Mapping(source = "id" , target = "userProfileId")
    UserProfileFindAllResponseDto toUserProfileFindAllResponseDto(Post userProfile);

    RegisterElasticModel toRegisterElasticModel(Post userProfile);
}
