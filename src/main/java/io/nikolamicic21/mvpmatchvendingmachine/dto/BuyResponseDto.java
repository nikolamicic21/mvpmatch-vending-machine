package io.nikolamicic21.mvpmatchvendingmachine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuyResponseDto {

    private Integer totalSpent;
    private String productName;
    private List<Integer> change;
}
