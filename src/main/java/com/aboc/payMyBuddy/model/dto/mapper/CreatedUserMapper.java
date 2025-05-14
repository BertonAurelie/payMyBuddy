package com.aboc.payMyBuddy.model.dto.mapper;

import com.aboc.payMyBuddy.model.UserDb;
import com.aboc.payMyBuddy.model.dto.request.CreatedUserDto;

/**
 * Mapper CreatedUserDto to user & user to CreatedUserDto.
 * Used for Post status of userService(createUser method).
 */
public class CreatedUserMapper {

    public CreatedUserMapper(){}

    public static UserDb toEntity(CreatedUserDto createdUserDto){
        UserDb userDb = new UserDb();

        userDb.setUsername(createdUserDto.getUsername());
        userDb.setEmail(createdUserDto.getEmail());
        userDb.setPassword(createdUserDto.getPassword());

        return userDb;
    }

    public static CreatedUserDto toDto(UserDb userDb){
        CreatedUserDto createdUserDto = new CreatedUserDto();

        createdUserDto.setUsername(userDb.getUsername());
        createdUserDto.setEmail(userDb.getEmail());
        createdUserDto.setPassword(userDb.getPassword());

        return createdUserDto;
    }
}
