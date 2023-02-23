package io.nikolamicic21.mvpmatchvendingmachine.service;

import io.nikolamicic21.mvpmatchvendingmachine.dto.CreateProductRequestDto;
import io.nikolamicic21.mvpmatchvendingmachine.dto.ProductResponseDto;
import io.nikolamicic21.mvpmatchvendingmachine.dto.UpdateProductRequestDto;
import io.nikolamicic21.mvpmatchvendingmachine.exception.ProductNotFoundException;
import io.nikolamicic21.mvpmatchvendingmachine.exception.UserNotFoundException;
import io.nikolamicic21.mvpmatchvendingmachine.model.Product;
import io.nikolamicic21.mvpmatchvendingmachine.repository.ProductRepository;
import io.nikolamicic21.mvpmatchvendingmachine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static io.nikolamicic21.mvpmatchvendingmachine.service.UserService.USER_WITH_USERNAME_NOT_FOUND_EXCEPTION_MESSAGE_FORMAT;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    public static final String PRODUCT_WITH_PRODUCT_ID_NOT_FOUND_EXCEPTION_MESSAGE_FORMAT = "Product with productId '%s' not found";
    public static final String PRODUCT_WITH_PRODUCT_ID_FOR_USER_WITH_USERNAME_NOT_FOUND_EXCEPTION_MESSAGE_FORMAT = "Product with productId '%s' not found for user '%s'";

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAll() {
        return productRepository.findAll().stream()
                .map(this::mapProductToProductResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getByProductId(UUID productId) {
        return productRepository.findByProductId(productId)
                .map(this::mapProductToProductResponseDto)
                .orElseThrow(() -> new ProductNotFoundException(String.format(PRODUCT_WITH_PRODUCT_ID_NOT_FOUND_EXCEPTION_MESSAGE_FORMAT, productId.toString())));
    }

    public ProductResponseDto create(CreateProductRequestDto productDto, String username) {
        return userRepository.findByUsername(username)
                .map(user -> mapCreateProductRequestDtoToProduct(productDto, user))
                .map(productRepository::save)
                .map(this::mapProductToProductResponseDto)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_WITH_USERNAME_NOT_FOUND_EXCEPTION_MESSAGE_FORMAT, username)));
    }

    public ProductResponseDto update(UUID productId, UpdateProductRequestDto productDto, String username) {
        return productRepository.findByProductIdAndSeller_Username(productId, username)
                .map(product -> updateProduct(product, productDto))
                .map(productRepository::save)
                .map(this::mapProductToProductResponseDto)
                .orElseThrow(() -> new ProductNotFoundException(String.format(PRODUCT_WITH_PRODUCT_ID_FOR_USER_WITH_USERNAME_NOT_FOUND_EXCEPTION_MESSAGE_FORMAT, productId.toString(), username)));
    }

    public void delete(UUID productId, String username) {
        productRepository.findByProductIdAndSeller_Username(productId, username)
                .ifPresentOrElse(
                        productRepository::delete,
                        () -> {
                            throw new ProductNotFoundException(String.format(PRODUCT_WITH_PRODUCT_ID_FOR_USER_WITH_USERNAME_NOT_FOUND_EXCEPTION_MESSAGE_FORMAT, productId.toString(), username));
                        }
                );
    }

    private ProductResponseDto mapProductToProductResponseDto(Product product) {
        return new ProductResponseDto(
                product.getAmountAvailable(),
                product.getCost(),
                product.getProductName(),
                product.getProductId()
        );
    }

    private Product mapCreateProductRequestDtoToProduct(CreateProductRequestDto productDto, io.nikolamicic21.mvpmatchvendingmachine.model.User user) {
        return new Product(
                productDto.getAmountAvailable(),
                productDto.getCost(),
                productDto.getProductName(),
                user
        );
    }

    private Product updateProduct(Product product, UpdateProductRequestDto productDto) {
        if (productDto.getProductName() != null) {
            product.setProductName(productDto.getProductName());
        }
        if (productDto.getAmountAvailable() != null) {
            product.setAmountAvailable(productDto.getAmountAvailable());
        }
        if (productDto.getCost() != null) {
            product.setCost(productDto.getCost());
        }

        return product;
    }
}
