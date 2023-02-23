package io.nikolamicic21.mvpmatchvendingmachine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepositResponseDto {

    private String username;
    private Integer deposit;

}
