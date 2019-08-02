package ru.sberbank.javaschool.edu.domain;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN,
    TEACHER,
    STUDENT;

    @Override
    public String getAuthority() {
        return name();
    }
}
