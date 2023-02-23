package io.nikolamicic21.mvpmatchvendingmachine.security;

import io.nikolamicic21.mvpmatchvendingmachine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> new User(user.getUsername(), user.getPassword(), buildAuthorities(user)))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    private Collection<SimpleGrantedAuthority> buildAuthorities(io.nikolamicic21.mvpmatchvendingmachine.model.User user) {
        return List.of(new SimpleGrantedAuthority(String.format("ROLE_%s", user.getRole().toString())));
    }
}
