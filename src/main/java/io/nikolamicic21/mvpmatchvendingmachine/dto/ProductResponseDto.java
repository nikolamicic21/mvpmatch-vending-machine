package io.nikolamicic21.mvpmatchvendingmachine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDto {

    private Integer amountAvailable;
    private Integer cost;
    private String productName;
    private UUID productId;
}
