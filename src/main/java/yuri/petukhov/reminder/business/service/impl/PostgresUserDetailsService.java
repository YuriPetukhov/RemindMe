package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Custom UserDetailsService implementation that retrieves user details from a PostgreSQL database.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostgresUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
                .map(user -> {
                    List<GrantedAuthority> authorities = user.getRoles().stream()
                            .map(role -> {
                                String authority = role.getAuthority();
                                return new SimpleGrantedAuthority(authority);
                            })
                            .collect(Collectors.toList());

                    return new org.springframework.security.core.userdetails.User(
                            user.getId().toString(),
                            passwordEncoder.encode(user.getChatId().toString()),
                            authorities);
                })
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }
}

