package com.example.passwordchecker.api;

import com.example.passwordchecker.user.DemoUser;
import com.example.passwordchecker.user.DemoUserDetailsService;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class UserAdministrationApi {

    private final CompromisedPasswordChecker compromisedPasswordChecker;
    private final PasswordEncoder passwordEncoder;
    private final DemoUserDetailsService userDetailsService;

    public UserAdministrationApi(
            CompromisedPasswordChecker compromisedPasswordChecker,
            PasswordEncoder passwordEncoder,
            DemoUserDetailsService userDetailsService) {
        this.compromisedPasswordChecker = compromisedPasswordChecker;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping({"/", "/userinfo", "/me"})
    public String userinfo(@AuthenticationPrincipal(errorOnInvalidType = true) DemoUser user) {
        return String.format("You are logged in as user [%s] with authorities '%s'!", user.getUsername(), user.getAuthorities());
    }

    @PostMapping("/check-password")
    public String checkPassword(@RequestBody CheckPasswordRequest checkPasswordRequest) {
        return String.format("You're given password is [%s]!",
                compromisedPasswordChecker.check(checkPasswordRequest.password()).isCompromised() ? "compromised" : "safe");
    }

    @PostMapping("/register")
    public String register(@RequestBody DemoUser user) {
        if (userDetailsService.userExists(user.getUsername())) {
            return "User already exists!";
        }

        List<String> errors = checkPasswordPolicies(user.getPassword());
        if (!errors.isEmpty()) return String.join(",", errors);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDetailsService.addUser(user);
        return "User registered successfully!";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, @AuthenticationPrincipal(errorOnInvalidType = true) DemoUser user) {

        if (!passwordEncoder.matches(changePasswordRequest.oldPassword(), user.getPassword())) {
            return "Old password is incorrect!";
        }

        List<String> errors = checkPasswordPolicies(changePasswordRequest.newPassword());
        if (!errors.isEmpty()) return String.join(",", errors);

        user.setPassword(passwordEncoder.encode(changePasswordRequest.newPassword()));

        return String.format("Password for user %s changed successfully!", user.getUsername());
    }

    private List<String> checkPasswordPolicies(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null) {
            errors.add("Password must be specified!");
            return errors;
        }

        if (password.length() < 12) {
            errors.add("Password length must be at least 12!");
        }
        if (password.length() > 100) {
            errors.add("Password length must not be > 100!");
        }
        CompromisedPasswordDecision decision = compromisedPasswordChecker.check(password);
        if (decision.isCompromised()) {
            errors.add("Given password is compromised!");
        }
        return errors;
    }

}
