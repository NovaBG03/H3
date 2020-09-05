package com.example.h3server.mappers;

import com.example.h3server.dtos.user.UserDataDTO;
import com.example.h3server.dtos.user.UserResponseDTO;
import com.example.h3server.models.Role;
import com.example.h3server.models.User;
import com.example.h3server.models.User.UserBuilder;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-09-05T19:59:30+0300",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 1.8.0_251 (Oracle Corporation)"
)
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponseDTO userToUserResponseDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponseDTO userResponseDTO = new UserResponseDTO();

        userResponseDTO.setId( user.getId() );
        userResponseDTO.setUsername( user.getUsername() );
        userResponseDTO.setEmail( user.getEmail() );
        List<Role> list = user.getRoles();
        if ( list != null ) {
            userResponseDTO.setRoles( new ArrayList<Role>( list ) );
        }

        return userResponseDTO;
    }

    @Override
    public User userDataDTOToUser(UserDataDTO userDataDTO) {
        if ( userDataDTO == null ) {
            return null;
        }

        UserBuilder user = User.builder();

        user.username( userDataDTO.getUsername() );
        user.email( userDataDTO.getEmail() );
        user.password( userDataDTO.getPassword() );

        return user.build();
    }
}
