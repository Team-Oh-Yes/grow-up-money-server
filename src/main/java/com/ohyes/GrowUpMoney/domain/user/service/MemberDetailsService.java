package com.ohyes.GrowUpMoney.domain.user.service;

import com.ohyes.GrowUpMoney.domain.user.entity.CustomUser;
import com.ohyes.GrowUpMoney.domain.user.exception.UserNotFoundException;
import com.ohyes.GrowUpMoney.domain.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var result = memberRepository.findByUsername(username);
        if (result.isEmpty()){
            throw new UserNotFoundException();
        }
        var user = result.get();

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));
        return new CustomUser(user.getId(), user.getUsername(), user.getPassword(), authorities);
    }

}
