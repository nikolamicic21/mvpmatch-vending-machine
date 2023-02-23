package io.nikolamicic21.mvpmatchvendingmachine.model;

import io.nikolamicic21.mvpmatchvendingmachine.exception.UserDepositIncrementException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Table(name = "mvpmatch_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mvpmatch_gen")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    @Setter
    private String username;

    @Column(name = "password", nullable = false, length = 60)
    @Setter
    private String password;

    @Column(name = "deposit", nullable = false)
    private Integer deposit;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @Setter
    private UserRole role;

    public User(String username, String password, UserRole role) {
        this.deposit = 0;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public void incrementDeposit(Integer depositIncrement) {
        if (depositIncrement == 5 || depositIncrement == 10 || depositIncrement == 20 || depositIncrement == 50 || depositIncrement == 100) {
            this.deposit += depositIncrement;
        } else {
            throw new UserDepositIncrementException("User's deposit can be incremented by 5, 10, 20, 50, or 100 only");
        }
    }

    public void decrementDeposit(Integer depositDecrement) {
        if (depositDecrement != 0) {
            this.deposit -= depositDecrement;
        }
    }

    public void resetDeposit() {
        this.deposit = 0;
    }
}
