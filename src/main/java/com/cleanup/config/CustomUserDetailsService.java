package com.cleanup.config;

import com.cleanup.model.Authority;
import com.cleanup.model.User;
import com.cleanup.service.interfaces.UserService;
import com.cleanup.utility.exceptions.NotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    private final UserService userService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        User user;
        try {
            user = name.contains("@") ? userService.findByEmail(name) : userService.findByUsername(name);
            if (user == null) throw new NotFoundException("Wrong username/email: " + name);
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException("Wrong username/email: " + name);
        }

        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (Authority authority : user.getAuthorities()) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority.getRole());
            grantedAuthorities.add(grantedAuthority);
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), grantedAuthorities);
    }
}
