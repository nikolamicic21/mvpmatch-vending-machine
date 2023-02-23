package io.nikolamicic21.mvpmatchvendingmachine.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositRequestDto {

    @NotNull
    @Min(0)
    private Integer deposit;

}
