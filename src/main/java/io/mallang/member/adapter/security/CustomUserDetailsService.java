package io.mallang.member.adapter.security;

import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.domain.Email;
import io.mallang.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final LoadMemberPort loadMemberPort;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = loadMemberPort.getByEmail(new Email(email));

        return new CustomUserDetails(member);
    }
}
