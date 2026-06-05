package com.campingmanager.users;

import com.campingmanager.users.entity.Admin;
import com.campingmanager.users.entity.Ospite;
import com.campingmanager.users.entity.Role;
import com.campingmanager.users.entity.Staff;
import com.campingmanager.users.entity.User;
import com.campingmanager.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserEntityTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldPersistAdminAndExposeRoleAuthority() {
        Admin admin = new Admin();
        admin.setEmail("admin@camping.it");
        admin.setPassword("hashed");
        admin.setName("Anna");
        admin.setSurname("Bianchi");

        userRepository.save(admin);

        User found = userRepository.findByEmail("admin@camping.it").orElseThrow();
        assertThat(found).isInstanceOf(Admin.class);
        assertThat(found.getRole()).isEqualTo(Role.ADMIN);
        assertThat(found.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");
        assertThat(userRepository.existsByEmail("admin@camping.it")).isTrue();
    }

    @Test
    void shouldPersistStaffWithDepartment() {
        Staff staff = new Staff();
        staff.setEmail("staff@camping.it");
        staff.setPassword("hashed");
        staff.setName("Luca");
        staff.setSurname("Verdi");
        staff.setDepartment(Staff.Department.RECEPTION);

        userRepository.save(staff);

        User found = userRepository.findByEmail("staff@camping.it").orElseThrow();
        assertThat(found).isInstanceOf(Staff.class);
        assertThat(found.getRole()).isEqualTo(Role.STAFF);
        assertThat(((Staff) found).getDepartment()).isEqualTo(Staff.Department.RECEPTION);
    }

    @Test
    void shouldPersistOspite() {
        Ospite ospite = new Ospite();
        ospite.setEmail("ospite@camping.it");
        ospite.setPassword("hashed");
        ospite.setName("Mario");
        ospite.setSurname("Rossi");
        ospite.setNationality("IT");

        userRepository.save(ospite);

        User found = userRepository.findByEmail("ospite@camping.it").orElseThrow();
        assertThat(found).isInstanceOf(Ospite.class);
        assertThat(found.getRole()).isEqualTo(Role.OSPITE);
    }
}
