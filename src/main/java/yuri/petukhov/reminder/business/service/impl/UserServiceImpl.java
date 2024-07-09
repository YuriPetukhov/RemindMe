package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuri.petukhov.reminder.business.enums.RoleName;
import yuri.petukhov.reminder.business.enums.UserCardInputState;
import yuri.petukhov.reminder.business.model.Role;
import yuri.petukhov.reminder.business.model.User;
import yuri.petukhov.reminder.business.repository.RoleRepository;
import yuri.petukhov.reminder.business.repository.UserRepository;
import yuri.petukhov.reminder.business.service.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static yuri.petukhov.reminder.business.enums.RoleName.ROLE_ADMIN;

/**
 * Service implementation for managing user-related operations.
 * This class provides methods to save, update, remove, and find users, as well as manage user roles and states.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    /**
     * Saves a user to the repository.
     * @param user The user to save.
     */

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    /**
     * Updates a user in the repository.
     * @param user The user to update.
     */

    @Override
    public void updateUser(User user) {
        saveUser(user);
    }

    /**
     * Removes a user from the repository by their ID.
     * @param id The ID of the user to remove.
     */

    @Override
    public void removeUser(Long id) {
        userRepository.delete(findUserById(id));
    }

    /**
     * Finds a user by their ID.
     * @param id The ID of the user to find.
     * @return The found user.
     */
    @Override
    public User findUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User with ID " + id + " not found.");
        }
        return optionalUser.get();
    }

    /**
     * Finds a user by their chat ID.
     * @param chatId The chat ID of the user to find.
     * @return An Optional containing the found user or empty if not found.
     */

    @Override
    public Optional<User> findUserByChatId(Long chatId) {
        return userRepository.findByChatId(chatId);
    }

    /**
     * Retrieves the list of role names of a user by their chat ID.
     * If the user is found, returns a list of their role names.
     * If the user is not found, a new user is created with the USER role
     * and a list containing only the USER role name is returned.
     * @param chatId The chat ID of the user.
     * @param userName The username of the user.
     * @return The list of RoleName states of the user.
     */

    @Transactional
    @Override
    public List<String> getUserRoles(Long chatId, String userName) {
        Optional<User> optUser = findUserByChatId(chatId);
        if (optUser.isPresent()) {
            User user = optUser.get();
            List<Role> roles = user.getRoles();
            List<String> roleNames = roles.stream()
                    .map(role -> role.getRoleName().name())
                    .collect(Collectors.toList());
            log.info("User roles were detected: " + roleNames);
            return roleNames;
        } else {
            createNewUser(chatId, userName);
            return Collections.singletonList(RoleName.ROLE_USER.name());
        }
    }




    /**
     * Sets the role of a user by their chat ID.
     * @param chatId The chat ID of the user.
     * @param state The new role to set for the user.
     */

    @Override
    public void setUserRole(Long chatId, RoleName state) {
        User user = findUserByChatId(chatId).orElseThrow();
        List<Role> roles = user.getRoles() != null ? user.getRoles() : new ArrayList<>();

        if (state.equals(ROLE_ADMIN)) {
            roles.clear();
            roles.add(new Role(ROLE_ADMIN));
        } else {
            if (roles.isEmpty()) {
                roles.add(new Role(RoleName.ROLE_USER));
            }
            else if (!roles.contains(new Role(ROLE_ADMIN))) {
                roles.add(new Role(state));
            }
        }

        user.setRoles(roles);
        saveUser(user);
    }



    /**
     * Sets the card input state of a user.
     * @param user The user to set the state for.
     * @param userCardInputState The new card input state to set.
     */

    @Override
    public void setCardInputState(User user, UserCardInputState userCardInputState) {
        user.setCardState(userCardInputState);
        saveUser(user);
    }

    /**
     * Gets the card input state of a user by their chat ID.
     * @param chatId The chat ID of the user.
     * @return The UserCardInputState of the user.
     */

    @Override
    public UserCardInputState getUserCardState(Long chatId) {
        User user = findUserByChatId(chatId).orElseThrow();
        return user.getCardState();
    }

    /**
     * Gets the user ID by their chat ID.
     * @param chatId The chat ID of the user.
     * @return The ID of the user.
     */

    @Override
    public Long getUserId(Long chatId) {
        User user = userRepository.findByChatId(chatId).orElseThrow();
        return user.getId();
    }

    /**
     * Creates a new user with the given chat ID and username.
     * @param chatId The chat ID for the new user.
     * @param userName The username for the new user.
     */
    @Override
    public void createNewUser(Long chatId, String userName) {
        User user = new User();
        user.setChatId(chatId);
        user.setUserName(userName);

        Role userRole = roleRepository.findByRoleName(RoleName.ROLE_USER);

        List<Role> roles = new ArrayList<>();
        roles.add(userRole);
        user.setRoles(roles);

        user.setCardState(UserCardInputState.NONE);
        log.info("A NEW user was saved");
        saveUser(user);
    }



    /**
     * Checks if a user is authorized by comparing authentication ID and user ID.
     * @param authId The authentication ID.
     * @param userId The user ID.
     * @return True if the IDs match, false otherwise.
     */
    public boolean isAuthorized(Long authId, Long userId) {
        return authId.equals(userId);
    }
}
