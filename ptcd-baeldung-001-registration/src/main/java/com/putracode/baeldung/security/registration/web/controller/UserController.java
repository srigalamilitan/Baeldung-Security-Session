package com.putracode.baeldung.security.registration.web.controller;

import com.putracode.baeldung.security.registration.security.ActiveUserStore;
import com.putracode.baeldung.security.registration.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Locale;

@Controller
public class UserController {
    @Autowired
    ActiveUserStore activeUserStore;
    @Autowired
    IUserService userService;

    @RequestMapping(value="/loggedUsers",method = RequestMethod.GET)
    public String getLoggedUsers(final Locale locale,final Model model){
        model.addAttribute("users",activeUserStore.getUsers());
        return "users";
    }
    @RequestMapping(value="/loggedUsersFromSessionRegistry",method = RequestMethod.GET)
    public String getLoggedusersFromSessionRegistry(final Locale locale,final Model model){
        model.addAttribute("users",userService.getUsersFromSessionRegistry());
        return "users";
    }

}
