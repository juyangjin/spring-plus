package org.example.expert.config;

import java.util.Collection;
import java.util.List;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    public Long getId() {
        return user.getId(); // id를 반환
    }

    public String getEmail() {
        return user.getEmail();
    }

    public UserRole getUserRole() {
        return user.getUserRole();
    }
}
