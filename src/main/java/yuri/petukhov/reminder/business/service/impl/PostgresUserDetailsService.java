package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.repository.UserRepository;

/**
 * Custom UserDetailsService implementation that retrieves user details from a PostgreSQL database.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostgresUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads the user by their username (in this case, the user ID) and returns user details.
     * If the user is not found, throws UsernameNotFoundException.
     * @param username The username (user ID) of the user to load.
     * @return UserDetails object containing the user's information.
     * @throws UsernameNotFoundException if the user is not found in the database.
     */

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findById(Long.parseLong(username))
                .map(user -> User.builder()
                        .username(user.getId().toString())
                        .password(passwordEncoder().encode(user.getChatId().toString()))
                        .roles(user.getRole().name())
                        .build()
                )
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    /**
     * Bean definition for the password encoder to be used in the application.
     * @return A BCryptPasswordEncoder instance.
     */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

