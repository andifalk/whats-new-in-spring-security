package com.example.onetimetoken;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DemoApi {

    @GetMapping("/demo")
    public String demo(@AuthenticationPrincipal User user) {
        return "Hello '" + user.getUsername() + "'!</br>" +
                "<p>You are authenticated with a One Time Token.</p>" +
                "You can use this endpoint to test your One Time Token authentication.</br>" +
                "Logout by clicking the link below:</br>" +
                "<a href=\"/logout\">Logout</a>";
    }
}
