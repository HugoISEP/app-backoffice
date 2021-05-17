package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.domain.UserPosition;
import com.mycompany.myapp.service.dto.UserDTO;
import com.mycompany.myapp.service.dto.UserPositionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserPositionMapper {

    @Mapping(source = "user", target = "user", qualifiedByName = "userAsDto")
    UserPositionDto asDto(UserPosition in);

    @Mapping(source = "user", target = "user", qualifiedByName = "userToDto")
    UserPosition fromDto(UserPositionDto in);


    @Named("userAsDto")
    default UserDTO asDto(User user) {
        return UserMapper.userToUserDTOStatic(user);
    }

    @Named("userToDto")
    default User fromDto(UserDTO dto) {
        return UserMapper.userDTOToUserStatic(dto);
    }

}
