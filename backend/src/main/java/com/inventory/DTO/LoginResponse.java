package com.inventory.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {

    private UserInfoDto user;

    private String access_token;

    @Getter
    @Setter
    public static class UserInfoDto {
        private String id;
        private String name;
        private String email;
        private String role;
    }

}
