package io.nikolamicic21.mvpmatchvendingmachine.controller;

import io.nikolamicic21.mvpmatchvendingmachine.dto.CreateProductRequestDto;
import io.nikolamicic21.mvpmatchvendingmachine.dto.ProductResponseDto;
import io.nikolamicic21.mvpmatchvendingmachine.dto.UpdateProductRequestDto;
import io.nikolamicic21.mvpmatchvendingmachine.exception.ProductCreationException;
import io.nikolamicic21.mvpmatchvendingmachine.exception.UserNotFoundException;
import io.nikolamicic21.mvpmatchvendingmachine.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static io.nikolamicic21.mvpmatchvendingmachine.config.ApplicationProperties.PRODUCT_ENDPOINT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(
        value = PRODUCT_ENDPOINT,
        produces = APPLICATION_JSON_VALUE,
        consumes = APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
class ProductController {

    private final ProductService productService;

    @GetMapping
    List<ProductResponseDto> getAll() {
        return productService.getAll();
    }

    @GetMapping("/{productId}")
    ProductResponseDto getByProductId(@PathVariable UUID productId) {
        return productService.getByProductId(productId);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    ProductResponseDto create(
            @Valid @RequestBody CreateProductRequestDto productDto,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            throw new UserNotFoundException("User must be authenticated");
        }

        try {
            return productService.create(productDto, user.getUsername());
        } catch (RuntimeException exception) {
            throw new ProductCreationException(String.format("Exception occurred while trying to create product with name '%s'", productDto.getProductName()));
        }
    }

    @PutMapping("/{productId}")
    ProductResponseDto update(
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateProductRequestDto productDto,
            @AuthenticationPrincipal User user
    ) {
        return productService.update(productId, productDto, user.getUsername());
    }

    @DeleteMapping("/{productId}")
    void delete(
            @PathVariable UUID productId,
            @AuthenticationPrincipal User user
    ) {
        productService.delete(productId, user.getUsername());
    }
}
