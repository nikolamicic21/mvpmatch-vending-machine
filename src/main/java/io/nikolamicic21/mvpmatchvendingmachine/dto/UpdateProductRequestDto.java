package io.nikolamicic21.mvpmatchvendingmachine.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateProductRequestDto {

    @Min(0)
    private Integer amountAvailable;

    @Min(0)
    private Integer cost;

    private String productName;
}
