package io.nikolamicic21.mvpmatchvendingmachine.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateUserRequestDto {

    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotNull
    private UserRoleDto role;

}
