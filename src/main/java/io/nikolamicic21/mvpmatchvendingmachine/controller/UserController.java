package io.nikolamicic21.mvpmatchvendingmachine.controller;

import io.nikolamicic21.mvpmatchvendingmachine.dto.CreateUserRequestDto;
import io.nikolamicic21.mvpmatchvendingmachine.dto.UpdateUserRequestDto;
import io.nikolamicic21.mvpmatchvendingmachine.dto.UserResponseDto;
import io.nikolamicic21.mvpmatchvendingmachine.exception.UserCreationException;
import io.nikolamicic21.mvpmatchvendingmachine.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import static io.nikolamicic21.mvpmatchvendingmachine.config.ApplicationProperties.USER_ENDPOINT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(
        value = USER_ENDPOINT,
        produces = APPLICATION_JSON_VALUE,
        consumes = APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
class UserController {

    private final UserService userService;

    @GetMapping
    UserResponseDto get(@AuthenticationPrincipal User user) {
        return userService.getByUsername(user.getUsername());
    }

    @PostMapping
    @ResponseStatus(CREATED)
    UserResponseDto create(@Valid @RequestBody CreateUserRequestDto user) {
        try {
            return userService.create(user);
        } catch (RuntimeException exception) {
            throw new UserCreationException(String.format("Exception occurred while trying to create user with username '%s'", user.getUsername()));
        }
    }

    @PutMapping
    UserResponseDto update(
            @Valid @RequestBody UpdateUserRequestDto userDto,
            @AuthenticationPrincipal User user
    ) {
        return userService.update(user.getUsername(), userDto);
    }

    @DeleteMapping
    void delete(@AuthenticationPrincipal User user) {
        userService.delete(user.getUsername());
    }
}
