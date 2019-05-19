package com.putracode.baeldung.security.registration.persistence.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Data
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;
    public Privilege(String name){
        this.name=name;
    }

    @ManyToMany(mappedBy = "privileges")
    private Collection<Role> roles;

}
