package com.ohyes.GrowUpMoney.domain.user.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUser implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUser(Long id, String username, String password,
                      Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public Long getMemberId() {return id;}

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {return authorities;}

    @Override public String getPassword() {return password;}

    @Override public String getUsername() {return username;}

    @Override public boolean isAccountNonExpired() {return true;}

    @Override public boolean isAccountNonLocked() {return true;}

    @Override public boolean isCredentialsNonExpired() {return true;}

    @Override public boolean isEnabled() {return true;}
}
