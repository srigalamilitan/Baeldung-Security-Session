package com.putracode.baeldung.security.registration.security;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ActiveUserStore {
    public List<String> users;
    public ActiveUserStore() {
        this.users = new ArrayList<>();
    }
}
