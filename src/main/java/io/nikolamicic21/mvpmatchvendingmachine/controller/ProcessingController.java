package io.nikolamicic21.mvpmatchvendingmachine.controller;

import io.nikolamicic21.mvpmatchvendingmachine.dto.BuyRequestDto;
import io.nikolamicic21.mvpmatchvendingmachine.dto.BuyResponseDto;
import io.nikolamicic21.mvpmatchvendingmachine.dto.DepositRequestDto;
import io.nikolamicic21.mvpmatchvendingmachine.dto.DepositResponseDto;
import io.nikolamicic21.mvpmatchvendingmachine.service.ProcessingService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static io.nikolamicic21.mvpmatchvendingmachine.config.ApplicationProperties.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(
        produces = APPLICATION_JSON_VALUE,
        consumes = APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
class ProcessingController {

    private final ProcessingService processingService;
    private final SessionRegistry sessionRegistry;
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();

    @PostMapping(DEPOSIT_ENDPOINT)
    DepositResponseDto deposit(
            @AuthenticationPrincipal User authenticatedUser,
            @Valid @RequestBody DepositRequestDto depositDto
    ) {
        return processingService.deposit(authenticatedUser.getUsername(), depositDto);
    }

    @PostMapping(BUY_ENDPOINT)
    BuyResponseDto buy(
            @AuthenticationPrincipal User authenticatedUser,
            @Valid @RequestBody BuyRequestDto buyDto
    ) {
        return processingService.buy(authenticatedUser.getUsername(), buyDto);
    }

    @PostMapping(RESET_ENDPOINT)
    void buy(@AuthenticationPrincipal User authenticatedUser) {
        processingService.resetDeposit(authenticatedUser.getUsername());
    }

    @PostMapping("/logout/all")
    void logout(@AuthenticationPrincipal User user, HttpSession session) {
        for (final var sessionInformation : sessionRegistry.getAllSessions(user, true)) {
            sessionRegistry.removeSessionInformation(sessionInformation.getSessionId());
        }
        session.invalidate();
        securityContextHolderStrategy.clearContext();
    }
}
