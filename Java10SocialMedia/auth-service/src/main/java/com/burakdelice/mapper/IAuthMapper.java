package com.burakdelice.mapper;

import com.burakdelice.dto.request.ActivateRequestDto;
import com.burakdelice.dto.request.RegisterRequestDto;
import com.burakdelice.dto.request.UserSaveRequestDto;
import com.burakdelice.dto.response.RegisterResponseDto;
import com.burakdelice.rabbitmq.model.ActivationModel;
import com.burakdelice.rabbitmq.model.MailModel;
import com.burakdelice.rabbitmq.model.RegisterModel;
import com.burakdelice.repository.entity.Auth;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IAuthMapper {

    IAuthMapper INSTANCE = Mappers.getMapper(IAuthMapper.class);
    Auth toAuth(RegisterRequestDto dto);
    RegisterResponseDto toRegisterResponseDto(Auth auth);
    @Mapping(source = "id",target = "authId")
    UserSaveRequestDto toUserSaveRequestDto(Auth auth);
    @Mapping(source = "id", target = "authId")
    RegisterModel toRegisterModel(Auth auth);
    ActivationModel toActivationModel(ActivateRequestDto dto);
    MailModel toMailModel(Auth auth);
}
