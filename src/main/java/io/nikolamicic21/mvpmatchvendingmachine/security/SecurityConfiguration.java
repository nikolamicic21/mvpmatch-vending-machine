package io.nikolamicic21.mvpmatchvendingmachine.security;

import io.nikolamicic21.mvpmatchvendingmachine.dto.UserRoleDto;
import io.nikolamicic21.mvpmatchvendingmachine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.SessionManagementFilter;

import static io.nikolamicic21.mvpmatchvendingmachine.config.ApplicationProperties.*;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
class SecurityConfiguration {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, SessionRegistry sessionRegistry) throws Exception {
        return http
                // ...
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                                .maximumSessions(-1)
//                        .expiredSessionStrategy(SessionInformationExpiredStrategy)
                                .sessionRegistry(sessionRegistry)
                )
                .httpBasic(httpBasic -> httpBasic
                        .realmName("Basic")
                )
                .authorizeHttpRequests(httpRequests -> httpRequests
                        // /user endpoint
                        .requestMatchers(HttpMethod.GET, USER_ENDPOINT).authenticated()
                        .requestMatchers(HttpMethod.POST, USER_ENDPOINT).permitAll()
                        .requestMatchers(HttpMethod.PUT, USER_ENDPOINT).authenticated()
                        .requestMatchers(HttpMethod.DELETE, USER_ENDPOINT).authenticated()
                        // /product/* endpoint
                        .requestMatchers(HttpMethod.GET, PRODUCT_ENDPOINT).permitAll()
                        .requestMatchers(HttpMethod.GET, PRODUCT_ENDPOINT + "/*").permitAll()
                        .requestMatchers(HttpMethod.POST, PRODUCT_ENDPOINT).hasRole(UserRoleDto.SELLER.name())
                        .requestMatchers(HttpMethod.PUT, PRODUCT_ENDPOINT + "/*").hasRole(UserRoleDto.SELLER.name())
                        .requestMatchers(HttpMethod.DELETE, PRODUCT_ENDPOINT + "/*").hasRole(UserRoleDto.SELLER.name())
                        // /deposit endpoint
                        .requestMatchers(HttpMethod.POST, DEPOSIT_ENDPOINT).hasRole(UserRoleDto.BUYER.name())
                        // /buy endpoint
                        .requestMatchers(HttpMethod.POST, BUY_ENDPOINT).hasRole(UserRoleDto.BUYER.name())
                        // /reset endpoint
                        .requestMatchers(HttpMethod.POST, RESET_ENDPOINT).hasRole(UserRoleDto.BUYER.name())
                        // /logout/all endpoint
                        .requestMatchers(HttpMethod.POST, LOGOUT_ALL_ENDPOINT).authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterAfter(new PreventMultipleConcurrentSessionPerUserFilter(sessionRegistry), SessionManagementFilter.class)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(UserRepository userRepository) {
        return new UserDetailsServiceImpl(userRepository);
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
}
