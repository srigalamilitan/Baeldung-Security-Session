package com.putracode.baeldung.security.registration.persistence.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToMany(mappedBy = "roles")
    private Collection<User> users;
    @ManyToMany
    @JoinTable(name = "roles_privileges",joinColumns = @JoinColumn(name = "role_id",referencedColumnName = "id"),inverseJoinColumns = @JoinColumn(name = "privilege_id",referencedColumnName = "id"))
    private Collection<Privilege> privileges;
    private String name;

}
