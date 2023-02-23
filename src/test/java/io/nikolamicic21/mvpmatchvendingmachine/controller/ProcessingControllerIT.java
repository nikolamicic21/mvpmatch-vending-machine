package io.nikolamicic21.mvpmatchvendingmachine.controller;

import io.nikolamicic21.mvpmatchvendingmachine.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.util.List;

import static io.nikolamicic21.mvpmatchvendingmachine.config.ApplicationProperties.*;
import static io.nikolamicic21.mvpmatchvendingmachine.dto.UserRoleDto.BUYER;
import static io.nikolamicic21.mvpmatchvendingmachine.dto.UserRoleDto.SELLER;

class ProcessingControllerIT extends AbstractIntegrationTest {

    public static final String SELLER_1_USERNAME = "username2";
    public static final String SELLER_1_PASSWORD = "password2";
    public static final String JSESSIONID_COOKIE_KEY = "JSESSIONID";

    @Test
    void givenCreatedUserWithRoleBuyer_whenDeposit_thenStatusIsOkAndResponseIsEqualsToExpected() {
        // given
        final var createUserRequest = new CreateUserRequestDto();
        createUserRequest.setUsername(BUYER_1_USERNAME);
        createUserRequest.setPassword(BUYER_1_PASSWORD);
        createUserRequest.setRole(BUYER);

        client.post()
                .uri(USER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(createUserRequest), CreateUserRequestDto.class)
                .exchange();

        final var depositRequest = new DepositRequestDto(
                100
        );

        final var expectedResponseBody = new DepositResponseDto(
                BUYER_1_USERNAME,
                100
        );

        // when
        client.post()
                .uri(DEPOSIT_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, buildBasicAuthorizationHeader(BUYER_1_USERNAME, BUYER_1_PASSWORD))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(depositRequest), DepositRequestDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(DepositResponseDto.class).isEqualTo(expectedResponseBody);
    }

    @Test
    void givenCreatedUserWithRoleBuyerUnAuthorized_whenDeposit_thenStatusIs401() {
        // given
        final var createUserRequest = new CreateUserRequestDto();
        createUserRequest.setUsername(BUYER_1_USERNAME);
        createUserRequest.setPassword(BUYER_1_PASSWORD);
        createUserRequest.setRole(BUYER);

        client.post()
                .uri(USER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(createUserRequest), CreateUserRequestDto.class)
                .exchange();

        final var depositRequest = new DepositRequestDto(
                100
        );

        final var expectedResponseBody = new DepositResponseDto(
                BUYER_1_USERNAME,
                100
        );

        // when
        client.post()
                .uri(DEPOSIT_ENDPOINT)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(depositRequest), DepositRequestDto.class)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void givenRequestedDepositIsNotOneOf5or10or20or50or100_whenDeposit_thenStatusIs400() {
        // given
        final var createUserRequest = new CreateUserRequestDto();
        createUserRequest.setUsername(BUYER_1_USERNAME);
        createUserRequest.setPassword(BUYER_1_PASSWORD);
        createUserRequest.setRole(BUYER);

        client.post()
                .uri(USER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(createUserRequest), CreateUserRequestDto.class)
                .exchange();

        final var depositRequest = new DepositRequestDto(
                25
        );

        // when
        client.post()
                .uri(DEPOSIT_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, buildBasicAuthorizationHeader(BUYER_1_USERNAME, BUYER_1_PASSWORD))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(depositRequest), DepositRequestDto.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().jsonPath("$.detail")
                .isEqualTo("User's deposit can be incremented by 5, 10, 20, 50, or 100 only");
    }

    @Test
    void givenCreatedUserWithBuyerRoleAnd100DepositAndProductWithAmount5WithCosts10Each_whenBuy2Products_thenStatusIsOkAndResponseEqualsToExpected() {
        // given
        final var createUserBuyerRequest = new CreateUserRequestDto();
        createUserBuyerRequest.setUsername(BUYER_1_USERNAME);
        createUserBuyerRequest.setPassword(BUYER_1_PASSWORD);
        createUserBuyerRequest.setRole(BUYER);

        client.post()
                .uri(USER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(createUserBuyerRequest), CreateUserRequestDto.class)
                .exchange();

        final var depositRequest = new DepositRequestDto(
                100
        );

        final var depositResponseCookies = client.post()
                .uri(DEPOSIT_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, buildBasicAuthorizationHeader(BUYER_1_USERNAME, BUYER_1_PASSWORD))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(depositRequest), DepositRequestDto.class)
                .exchange()
                .returnResult(DepositResponseDto.class)
                .getResponseCookies();

        final var createUserSellerRequest = new CreateUserRequestDto();
        createUserSellerRequest.setUsername(SELLER_1_USERNAME);
        createUserSellerRequest.setPassword(SELLER_1_PASSWORD);
        createUserSellerRequest.setRole(SELLER);

        client.post()
                .uri(USER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(createUserSellerRequest), CreateUserRequestDto.class)
                .exchange();

        final var createProductRequest = new CreateProductRequestDto(
                5,
                10,
                "product-1"
        );

        final var createdProductResponse = client.post()
                .uri(PRODUCT_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, buildBasicAuthorizationHeader(SELLER_1_USERNAME, SELLER_1_PASSWORD))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(createProductRequest), CreateProductRequestDto.class)
                .exchange()
                .expectBody(ProductResponseDto.class).returnResult().getResponseBody();

        final var buyRequest = new BuyRequestDto(
                createdProductResponse.getProductId(),
                2
        );

        final var expectedResponseBody = new BuyResponseDto(
                20,
                "product-1",
                List.of(10, 20, 50)
        );

        // when
        client.post()
                .uri(BUY_ENDPOINT)
                .cookie(JSESSIONID_COOKIE_KEY, depositResponseCookies.get(JSESSIONID_COOKIE_KEY).get(0).getValue())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(buyRequest), BuyRequestDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BuyResponseDto.class).isEqualTo(expectedResponseBody);
    }


    @Test
    void givenCreatedUserWithBuyerRoleAnd100DepositAndProductWithAmount5WithCosts100Each_whenBuy5Products_thenStatusIs400AndResponseEqualsToExpected() {
        // given
        final var createUserBuyerRequest = new CreateUserRequestDto();
        createUserBuyerRequest.setUsername(BUYER_1_USERNAME);
        createUserBuyerRequest.setPassword(BUYER_1_PASSWORD);
        createUserBuyerRequest.setRole(BUYER);

        client.post()
                .uri(USER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(createUserBuyerRequest), CreateUserRequestDto.class)
                .exchange();

        final var depositRequest = new DepositRequestDto(
                100
        );

        final var depositResponseCookies = client.post()
                .uri(DEPOSIT_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, buildBasicAuthorizationHeader(BUYER_1_USERNAME, BUYER_1_PASSWORD))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(depositRequest), DepositRequestDto.class)
                .exchange()
                .returnResult(DepositResponseDto.class)
                .getResponseCookies();

        final var createUserSellerRequest = new CreateUserRequestDto();
        createUserSellerRequest.setUsername(SELLER_1_USERNAME);
        createUserSellerRequest.setPassword(SELLER_1_PASSWORD);
        createUserSellerRequest.setRole(SELLER);

        client.post()
                .uri(USER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(createUserSellerRequest), CreateUserRequestDto.class)
                .exchange();

        final var createProductRequest = new CreateProductRequestDto(
                5,
                100,
                "product-1"
        );

        final var createdProductResponse = client.post()
                .uri(PRODUCT_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, buildBasicAuthorizationHeader(SELLER_1_USERNAME, SELLER_1_PASSWORD))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(createProductRequest), CreateProductRequestDto.class)
                .exchange()
                .expectBody(ProductResponseDto.class).returnResult().getResponseBody();

        final var buyRequest = new BuyRequestDto(
                createdProductResponse.getProductId(),
                5
        );

        // when
        client.post()
                .uri(BUY_ENDPOINT)
                .cookie(JSESSIONID_COOKIE_KEY, depositResponseCookies.get(JSESSIONID_COOKIE_KEY).get(0).getValue())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(buyRequest), BuyRequestDto.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().jsonPath("$.detail").isEqualTo("You have less than '500' coins on your account");
    }
}
