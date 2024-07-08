package yuri.petukhov.reminder.business.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import yuri.petukhov.reminder.business.enums.RoleName;

@Entity(name = "roles")
@Data
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleName roleName;

    public Role(RoleName roleName) {
        this.roleName = roleName;
    }

    public String getAuthority() {
        return roleName.name();
    }


}
