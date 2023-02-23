package io.nikolamicic21.mvpmatchvendingmachine.repository;

import io.nikolamicic21.mvpmatchvendingmachine.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

}
