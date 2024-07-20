package yuri.petukhov.reminder.business.service;

import yuri.petukhov.reminder.business.enums.RoleName;
import yuri.petukhov.reminder.business.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Optional<Role> findByRoleName(RoleName roleName);

    Role getHighestPriorityRole(List<Role> userRoles);
}
