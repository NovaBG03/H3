package com.example.h3server.mappers;

import com.example.h3server.dtos.UserDataDTO;
import com.example.h3server.dtos.UserResponseDTO;
import com.example.h3server.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserResponseDTO userToUserResponseDTO(User user);

    // @Mapping(target = "")
    User userDataDTOToUser(UserDataDTO userDataDTO);
}
