package io.nikolamicic21.mvpmatchvendingmachine.dto;

import lombok.Data;

@Data
public class UpdateUserRequestDto {

    private String username;
    private String password;
    private UserRoleDto role;
}
