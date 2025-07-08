package com.aboc.payMyBuddy.model.dto.mapper;

import com.aboc.payMyBuddy.model.UserDb;
import com.aboc.payMyBuddy.model.dto.request.UpdatedUserDto;

/**
 * Mapper UpdatedUserMapper to user & user to UpdatedUserMapper.
 * Used for Updating status of userService(UpdatedUserMapper method).
 */
public class UpdatedUserMapper {

    public UpdatedUserMapper() {
    }

    public static UserDb toEntity(UpdatedUserDto userDto) {
        UserDb userDb = new UserDb();

        userDb.setUsername(userDto.getUsername());
        userDb.setEmail(userDto.getEmail());
        userDb.setPassword(userDto.getPassword());
        userDb.setSolde(userDto.getSolde());

        return userDb;
    }

    public static UpdatedUserDto toDto(UserDb userDb) {
        UpdatedUserDto userDto = new UpdatedUserDto();

        userDto.setId(userDb.getId());
        userDto.setUsername(userDb.getUsername());
        userDto.setEmail(userDb.getEmail());
        userDto.setPassword(userDb.getPassword());
        userDto.setSolde(userDb.getSolde());

        return userDto;
    }


}
