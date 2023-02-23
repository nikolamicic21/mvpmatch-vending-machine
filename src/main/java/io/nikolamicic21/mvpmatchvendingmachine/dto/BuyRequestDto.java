package io.nikolamicic21.mvpmatchvendingmachine.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuyRequestDto {

    @NotNull
    private UUID productId;

    @NotNull
    @Min(1)
    private Integer amount;

}
