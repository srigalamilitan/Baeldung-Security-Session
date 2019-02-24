package com.putracode.baeldung.security.registration.security;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.util.List;

@Component
@Data
public class LoggedUser implements HttpSessionBindingListener {

    private String username;
    private ActiveUserStore activeUserStore;

    public LoggedUser(String username, ActiveUserStore activeUserStore) {
        this.username = username;
        this.activeUserStore = activeUserStore;
    }

    public LoggedUser() {
    }

    @Override
    public void valueBound(HttpSessionBindingEvent httpSessionBindingEvent) {
        List<String> users=activeUserStore.getUsers();
        LoggedUser user=(LoggedUser) httpSessionBindingEvent.getValue();
        if(!users.contains(user.getUsername())){
            users.add(user.getUsername());
        }
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent httpSessionBindingEvent) {
        List<String> users = activeUserStore.getUsers();
        LoggedUser user = (LoggedUser) httpSessionBindingEvent.getValue();
        if (users.contains(user.getUsername())) {
            users.remove(user.getUsername());
        }
    }
}
