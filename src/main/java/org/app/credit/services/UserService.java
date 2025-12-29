package org.app.credit.services;

import org.app.credit.entities.dtos.UserRegisterDto;
import org.app.credit.entities.dtos.UserResponseDto;

public interface UserService {

    UserResponseDto save(UserRegisterDto user);

}
