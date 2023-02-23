package io.nikolamicic21.mvpmatchvendingmachine.service;

import io.nikolamicic21.mvpmatchvendingmachine.dto.BuyRequestDto;
import io.nikolamicic21.mvpmatchvendingmachine.dto.BuyResponseDto;
import io.nikolamicic21.mvpmatchvendingmachine.dto.DepositRequestDto;
import io.nikolamicic21.mvpmatchvendingmachine.dto.DepositResponseDto;
import io.nikolamicic21.mvpmatchvendingmachine.exception.ProductAmountNotAvailableException;
import io.nikolamicic21.mvpmatchvendingmachine.exception.ProductNotFoundException;
import io.nikolamicic21.mvpmatchvendingmachine.exception.UserInsufficientDepositException;
import io.nikolamicic21.mvpmatchvendingmachine.exception.UserNotFoundException;
import io.nikolamicic21.mvpmatchvendingmachine.model.User;
import io.nikolamicic21.mvpmatchvendingmachine.repository.ProductRepository;
import io.nikolamicic21.mvpmatchvendingmachine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

import static io.nikolamicic21.mvpmatchvendingmachine.service.ProductService.PRODUCT_WITH_PRODUCT_ID_NOT_FOUND_EXCEPTION_MESSAGE_FORMAT;
import static io.nikolamicic21.mvpmatchvendingmachine.service.UserService.USER_WITH_USERNAME_NOT_FOUND_EXCEPTION_MESSAGE_FORMAT;

@Service
@Transactional
@RequiredArgsConstructor
public class ProcessingService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public DepositResponseDto deposit(String username, DepositRequestDto depositDto) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    user.incrementDeposit(depositDto.getDeposit());
                    return mapUserToDepositResponseDto(user);
                })
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_WITH_USERNAME_NOT_FOUND_EXCEPTION_MESSAGE_FORMAT, username)));
    }

    public BuyResponseDto buy(String username, BuyRequestDto buyDto) {
        final var productToBuy = productRepository.findByProductId(buyDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(String.format(PRODUCT_WITH_PRODUCT_ID_NOT_FOUND_EXCEPTION_MESSAGE_FORMAT, buyDto.getProductId().toString())));
        final var buyer = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_WITH_USERNAME_NOT_FOUND_EXCEPTION_MESSAGE_FORMAT, username)));

        if (productToBuy.getAmountAvailable() < buyDto.getAmount()) {
            throw new ProductAmountNotAvailableException(String.format("There is less than '%d' products with name '%s' available", buyDto.getAmount(), productToBuy.getProductName()));
        } else if (buyer.getDeposit() < (productToBuy.getCost() * buyDto.getAmount())) {
            throw new UserInsufficientDepositException(String.format("You have less than '%d' coins on your account", productToBuy.getCost() * buyDto.getAmount()));
        } else {
            productToBuy.setAmountAvailable(productToBuy.getAmountAvailable() - buyDto.getAmount());
            buyer.decrementDeposit(productToBuy.getCost() * buyDto.getAmount());
            productRepository.save(productToBuy);
            userRepository.save(buyer);
        }

        final var response = new BuyResponseDto();
        response.setProductName(productToBuy.getProductName());
        response.setTotalSpent(productToBuy.getCost() * buyDto.getAmount());
        final var change = buildChangeArray(buyer);
        response.setChange(change);

        return response;
    }

    public void resetDeposit(String username) {
        userRepository.findByUsername(username)
                .ifPresentOrElse(
                        user -> {
                            user.resetDeposit();
                            userRepository.save(user);
                        },
                        () -> {
                            throw new UserNotFoundException(String.format(USER_WITH_USERNAME_NOT_FOUND_EXCEPTION_MESSAGE_FORMAT, username));
                        }
                );
    }

    private DepositResponseDto mapUserToDepositResponseDto(User user) {
        return new DepositResponseDto(
                user.getUsername(),
                user.getDeposit()
        );
    }

    private List<Integer> buildChangeArray(User buyer) {
        final var change = new LinkedList<Integer>();
        var deposit = buyer.getDeposit();
        while (deposit / 100 != 0) {
            change.addFirst(100);
            deposit %= 100;
        }
        while (deposit / 50 != 0) {
            change.addFirst(50);
            deposit %= 50;
        }
        while (deposit / 20 != 0) {
            change.addFirst(20);
            deposit %= 20;
        }
        while (deposit / 10 != 0) {
            change.addFirst(10);
            deposit %= 10;
        }
        while (deposit / 5 != 0) {
            change.addFirst(5);
            deposit %= 5;
        }

        return change;
    }
}
