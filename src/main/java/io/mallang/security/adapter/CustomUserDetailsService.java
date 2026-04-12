package io.mallang.security.adapter;

import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.domain.Email;
import io.mallang.member.domain.Member;
import io.mallang.member.domain.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final LoadMemberPort loadMemberPort;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            Member member = loadMemberPort.getByEmail(new Email(email));
            return new CustomUserDetails(member);
        } catch (MemberNotFoundException ex) {
            log.info("Authentication member not found: {}", ex.getMessage());
            throw new UsernameNotFoundException(ex.getMessage(), ex);
        }
    }
}
