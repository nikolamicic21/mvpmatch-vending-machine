package io.nikolamicic21.mvpmatchvendingmachine.controller;

import io.nikolamicic21.mvpmatchvendingmachine.dto.CreateUserRequestDto;
import io.nikolamicic21.mvpmatchvendingmachine.dto.UpdateUserRequestDto;
import io.nikolamicic21.mvpmatchvendingmachine.dto.UserResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import static io.nikolamicic21.mvpmatchvendingmachine.config.ApplicationProperties.USER_ENDPOINT;
import static io.nikolamicic21.mvpmatchvendingmachine.dto.UserRoleDto.BUYER;
import static io.nikolamicic21.mvpmatchvendingmachine.dto.UserRoleDto.SELLER;

class UserControllerIT extends AbstractIntegrationTest {

    @Test
    void givenValidCreateUserRequest_whenCreateUser_thenStatusIsCreated() {
        // given
        final var createUserRequest = new CreateUserRequestDto();
        createUserRequest.setUsername(BUYER_1_USERNAME);
        createUserRequest.setPassword(BUYER_1_PASSWORD);
        createUserRequest.setRole(BUYER);

        final var expectedResponseBody = new UserResponseDto(
                BUYER_1_USERNAME,
                0,
                BUYER
        );

        // when
        client.post()
                .uri(USER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(createUserRequest), CreateUserRequestDto.class)
                .exchange()
                // then
                .expectStatus().isCreated()
                .expectBody(UserResponseDto.class).isEqualTo(expectedResponseBody);
    }

    @Test
    void givenCreatedUserAndUnauthorizedRequest_whenGetUser_thenStatus401() {
        // given
        final var createUserRequest = new CreateUserRequestDto();
        createUserRequest.setUsername(BUYER_1_USERNAME);
        createUserRequest.setPassword(BUYER_1_PASSWORD);
        createUserRequest.setRole(BUYER);

        final var expectedResponseBody = new UserResponseDto(
                BUYER_1_USERNAME,
                0,
                BUYER
        );

        client.post()
                .uri(USER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(createUserRequest), CreateUserRequestDto.class)
                .exchange()
                // then
                .expectStatus().isCreated()
                .expectBody(UserResponseDto.class).isEqualTo(expectedResponseBody);

        // when
        client.get()
                .uri(USER_ENDPOINT)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                // then
                .expectStatus().isUnauthorized();
    }

    @Test
    void givenCreatedUser_whenGetUser_thenResponseBodyEqualsToExpectedUser() {
        // given
        final var createUserRequest = new CreateUserRequestDto();
        createUserRequest.setUsername(BUYER_1_USERNAME);
        createUserRequest.setPassword(BUYER_1_PASSWORD);
        createUserRequest.setRole(BUYER);

        final var expectedResponseBody = new UserResponseDto(
                BUYER_1_USERNAME,
                0,
                BUYER
        );

        client.post()
                .uri(USER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(createUserRequest), CreateUserRequestDto.class)
                .exchange()
                // then
                .expectStatus().isCreated()
                .expectBody(UserResponseDto.class).isEqualTo(expectedResponseBody);

        // when
        client.get()
                .uri(USER_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, buildBasicAuthorizationHeader(BUYER_1_USERNAME, BUYER_1_PASSWORD))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody(UserResponseDto.class).isEqualTo(expectedResponseBody);
    }

    @Test
    void givenCreatedUserAndUnauthorizedRequest_whenUpdateUser_thenStatusIs401() {
        // given
        final var createUserRequest = new CreateUserRequestDto();
        createUserRequest.setUsername(BUYER_1_USERNAME);
        createUserRequest.setPassword(BUYER_1_PASSWORD);
        createUserRequest.setRole(BUYER);

        final var expectedResponseBody = new UserResponseDto(
                BUYER_1_USERNAME,
                0,
                BUYER
        );

        client.post()
                .uri(USER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(createUserRequest), CreateUserRequestDto.class)
                .exchange()
                // then
                .expectStatus().isCreated()
                .expectBody(UserResponseDto.class).isEqualTo(expectedResponseBody);

        // when
        client.put()
                .uri(USER_ENDPOINT)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                // then
                .expectStatus().isUnauthorized();
    }

    @Test
    void givenCreatedUser_whenUpdateUser_thenStatusIsOkAndResponseBodyIsEqualsToExpectedUserDto() {
        // given
        final var createUserRequest = new CreateUserRequestDto();
        createUserRequest.setUsername(BUYER_1_USERNAME);
        createUserRequest.setPassword(BUYER_1_PASSWORD);
        createUserRequest.setRole(BUYER);

        final var expectedResponseBody = new UserResponseDto(
                BUYER_1_USERNAME,
                0,
                BUYER
        );

        client.post()
                .uri(USER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(createUserRequest), CreateUserRequestDto.class)
                .exchange()
                // then
                .expectStatus().isCreated()
                .expectBody(UserResponseDto.class).isEqualTo(expectedResponseBody);

        final var updateUserDto = new UpdateUserRequestDto();
        updateUserDto.setUsername(BUYER_1_USERNAME_UPDATED);
        updateUserDto.setPassword(BUYER_1_PASSWORD_UPDATED);
        updateUserDto.setRole(SELLER);

        final var expectedUpdatedUser = new UserResponseDto(
                BUYER_1_USERNAME_UPDATED,
                0,
                SELLER
        );

        // when
        client.put()
                .uri(USER_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, buildBasicAuthorizationHeader(BUYER_1_USERNAME, BUYER_1_PASSWORD))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(updateUserDto), UpdateUserRequestDto.class)
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody(UserResponseDto.class).isEqualTo(expectedUpdatedUser);
    }

    @Test
    void givenCreatedUserAndUnauthorizedRequest_whenDeleteUser_thenStatusIs401() {
        // given
        final var createUserRequest = new CreateUserRequestDto();
        createUserRequest.setUsername(BUYER_1_USERNAME);
        createUserRequest.setPassword(BUYER_1_PASSWORD);
        createUserRequest.setRole(BUYER);

        final var expectedResponseBody = new UserResponseDto(
                BUYER_1_USERNAME,
                0,
                BUYER
        );

        client.post()
                .uri(USER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(createUserRequest), CreateUserRequestDto.class)
                .exchange()
                // then
                .expectStatus().isCreated()
                .expectBody(UserResponseDto.class).isEqualTo(expectedResponseBody);

        // when
        client.delete()
                .uri(USER_ENDPOINT)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                // then
                .expectStatus().isUnauthorized();
    }

    @Test
    void givenCreatedUser_whenDeleteUserAndGetUser_thenStatusIsOkAndGetUserReturnsStatus401() {
        // given
        final var createUserRequest = new CreateUserRequestDto();
        createUserRequest.setUsername(BUYER_1_USERNAME);
        createUserRequest.setPassword(BUYER_1_PASSWORD);
        createUserRequest.setRole(BUYER);

        final var expectedResponseBody = new UserResponseDto(
                BUYER_1_USERNAME,
                0,
                BUYER
        );

        client.post()
                .uri(USER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(createUserRequest), CreateUserRequestDto.class)
                .exchange()
                // then
                .expectStatus().isCreated()
                .expectBody(UserResponseDto.class).isEqualTo(expectedResponseBody);

        // when
        client.delete()
                .uri(USER_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, buildBasicAuthorizationHeader(BUYER_1_USERNAME, BUYER_1_PASSWORD))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                // then
                .expectStatus().isOk();

        client.get()
                .uri(USER_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, buildBasicAuthorizationHeader(BUYER_1_USERNAME, BUYER_1_PASSWORD))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                // then
                .expectStatus().isUnauthorized();
    }
}
