package com.putracode.baeldung.security.registration.spring;

import com.putracode.baeldung.security.registration.persistence.dao.PrivilegeRepository;
import com.putracode.baeldung.security.registration.persistence.dao.RoleRepository;
import com.putracode.baeldung.security.registration.persistence.dao.UserRepository;
import com.putracode.baeldung.security.registration.persistence.model.Privilege;
import com.putracode.baeldung.security.registration.persistence.model.Role;
import com.putracode.baeldung.security.registration.persistence.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    private boolean alreadySetup=false;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(alreadySetup){
            return;
        }
        // == create initial privileges
        final Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        final Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
        final Privilege passwordPrivilege = createPrivilegeIfNotFound("CHANGE_PASSWORD_PRIVILEGE");

        // == create initial roles
        final List<Privilege> adminPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, writePrivilege, passwordPrivilege));
        final List<Privilege> userPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, passwordPrivilege));
        final Role adminRole = createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_USER", userPrivileges);

        // == create initial user
        createUserIfNotFound("test@test.com", "Test", "Test", "test", new ArrayList<Role>(Arrays.asList(adminRole)));

        alreadySetup = true;
//        final Privilege readPrivilege=create
    }



    @Transactional
    public Privilege createPrivilegeIfNotFound(final String name){
        Privilege privilege=privilegeRepository.findByName(name);
        if(privilege==null){
            privilege=new Privilege(name);
            privilege=privilegeRepository.save(privilege);
        }
        return privilege;
    }
    @Transactional
    public Role createRoleIfNotFound(final String name, final Collection<Privilege> privileges){
        Role role=roleRepository.findByName(name);
        if(role==null){
            role=new Role();
            role.setName(name);
        }
        role.setPrivileges(privileges);
        role=roleRepository.save(role);
        return role;
    }
    @Transactional
    public User createUserIfNotFound(final String email, final String firstName, final String lastname, final String password, final Collection<Role> roles){
        User user=userRepository.findByEmail(email);
        if(user==null){
            user=new User();
            user.setFirstName(firstName);
            user.setLastName(lastname);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(email);
            user.setEnabled(true);
        }
        user.setRoles(roles);
        user=userRepository.save(user);
        return user;
    }

}
