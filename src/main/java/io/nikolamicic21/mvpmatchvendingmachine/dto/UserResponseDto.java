package io.nikolamicic21.mvpmatchvendingmachine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private String username;
    private Integer deposit;
    private UserRoleDto role;

}
