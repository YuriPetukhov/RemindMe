package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.enums.RoleName;
import yuri.petukhov.reminder.business.model.Role;
import yuri.petukhov.reminder.business.repository.RoleRepository;
import yuri.petukhov.reminder.business.service.RoleService;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    @Override
    public Optional<Role> findByRoleName(RoleName roleName) {
        return roleRepository.findByRoleName(roleName);
    }

    @Override
    public Role getHighestPriorityRole(List<Role> userRoles) {
        return userRoles.stream()
                .max(Comparator.comparingInt(role -> role.getRoleName().getPriority()))
                .orElseThrow(NoSuchElementException::new);
    }
}
