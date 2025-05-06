package com.aboc.payMyBuddy.model.dto.mapper;

import com.aboc.payMyBuddy.model.User;
import com.aboc.payMyBuddy.model.dto.request.CreatedUserDto;

/**
 * Mapper CreatedUserDto to user & user to CreatedUserDto.
 * Used for Post status of userService(createUser method).
 */
public class CreatedUserMapper {

    public CreatedUserMapper(){}

    public static User toEntity(CreatedUserDto createdUserDto){
        User user = new User();

        user.setUsername(createdUserDto.getUsername());
        user.setEmail(createdUserDto.getEmail());
        user.setPassword(createdUserDto.getPassword());

        return user;
    }

    public static CreatedUserDto toDto(User user){
        CreatedUserDto createdUserDto = new CreatedUserDto();

        createdUserDto.setUsername(user.getUsername());
        createdUserDto.setEmail(user.getEmail());
        createdUserDto.setPassword(user.getPassword());

        return createdUserDto;
    }
}
