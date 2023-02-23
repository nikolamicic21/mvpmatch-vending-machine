package io.nikolamicic21.mvpmatchvendingmachine.service;

import io.nikolamicic21.mvpmatchvendingmachine.dto.CreateUserRequestDto;
import io.nikolamicic21.mvpmatchvendingmachine.dto.UpdateUserRequestDto;
import io.nikolamicic21.mvpmatchvendingmachine.dto.UserResponseDto;
import io.nikolamicic21.mvpmatchvendingmachine.dto.UserRoleDto;
import io.nikolamicic21.mvpmatchvendingmachine.exception.UserNotFoundException;
import io.nikolamicic21.mvpmatchvendingmachine.model.User;
import io.nikolamicic21.mvpmatchvendingmachine.model.UserRole;
import io.nikolamicic21.mvpmatchvendingmachine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    public static final String USER_WITH_USERNAME_NOT_FOUND_EXCEPTION_MESSAGE_FORMAT = "User with username '%s' not found";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponseDto getByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::mapUserToUserResponseDto)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_WITH_USERNAME_NOT_FOUND_EXCEPTION_MESSAGE_FORMAT, username)));
    }

    public UserResponseDto create(CreateUserRequestDto userDto) {
        final var user = mapCreateUserRequestDtoToUser(userDto);
        final var savedUser = this.userRepository.save(user);

        return mapUserToUserResponseDto(savedUser);
    }

    public UserResponseDto update(String username, UpdateUserRequestDto userDto) {
        return userRepository.findByUsername(username)
                .map(user -> updateUser(user, userDto))
                .map(userRepository::save)
                .map(this::mapUserToUserResponseDto)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_WITH_USERNAME_NOT_FOUND_EXCEPTION_MESSAGE_FORMAT, username)));
    }

    public void delete(String username) {
        userRepository.findByUsername(username)
                .ifPresentOrElse(
                        userRepository::delete,
                        () -> {
                            throw new UserNotFoundException(String.format(USER_WITH_USERNAME_NOT_FOUND_EXCEPTION_MESSAGE_FORMAT, username));
                        }
                );
    }

    private UserResponseDto mapUserToUserResponseDto(User user) {
        return new UserResponseDto(
                user.getUsername(),
                user.getDeposit(),
                UserRoleDto.valueOf(user.getRole().name())
        );
    }

    private User mapCreateUserRequestDtoToUser(CreateUserRequestDto userDto) {
        return new User(
                userDto.getUsername(),
                passwordEncoder.encode(userDto.getPassword()),
                UserRole.valueOf(userDto.getRole().name())
        );
    }

    private User updateUser(User user, UpdateUserRequestDto userDto) {
        if (userDto.getUsername() != null) {
            user.setUsername(userDto.getUsername());
        }
        if (userDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        if (userDto.getRole() != null) {
            user.setRole(UserRole.valueOf(userDto.getRole().name()));
        }

        return user;
    }
}
