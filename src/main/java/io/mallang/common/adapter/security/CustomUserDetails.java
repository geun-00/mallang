package io.mallang.common.adapter.security;

import io.mallang.member.domain.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Member member;

    public CustomUserDetails(Member member) {
        this.member = member;
    }

    public String getMemberIdValue() {
        return member.getId().value();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return member.getEmail().address();
    }

    @Override
    public String getPassword() {
        return member.getPassword().value();
    }
}
