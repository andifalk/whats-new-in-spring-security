package com.example.passwordchecker.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DemoUserDetailsService implements UserDetailsService {

    private final Map<String, DemoUser> usersMap = new HashMap<>();

    public DemoUserDetailsService() {
    }

    public DemoUserDetailsService(Set<DemoUser> users) {
        users.forEach(user -> usersMap.put(user.getUsername(), user));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        DemoUser user = usersMap.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        } else {
            return user;
        }
    }

    public boolean userExists(String username) {
        return usersMap.containsKey(username);
    }

    public void addUser(DemoUser user) {
        usersMap.put(user.getUsername(), user);
    }
}
