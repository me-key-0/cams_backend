package com.cams.user_service.dto;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class UserDto {
    private String username;
    private String email;
    private String password;

}
