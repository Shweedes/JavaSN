package com.example.javasocialnetwork.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final User user; // твоя сущность

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // если у тебя нет ролей — можно вернуть пустой список
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // предполагается, что у тебя есть поле password
    }

    @Override
    public String getUsername() {
        return user.getUsername(); // предполагается, что есть поле username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // можешь кастомизировать под логику блокировки
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // аналогично
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User getUser() {
        return user;
    }
}

