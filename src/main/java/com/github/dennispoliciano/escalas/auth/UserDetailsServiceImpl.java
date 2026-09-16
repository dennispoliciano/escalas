package com.github.dennispoliciano.escalas.auth;

import com.github.dennispoliciano.escalas.orgmembership.OrgMembershipRepository;
import com.github.dennispoliciano.escalas.user.User;
import com.github.dennispoliciano.escalas.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final OrgMembershipRepository orgMembershipRepository;

    public UserDetailsServiceImpl(UserRepository userRepository, OrgMembershipRepository orgMembershipRepository) {
        this.userRepository = userRepository;
        this.orgMembershipRepository = orgMembershipRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
        return new UserPrincipal(user,orgMembershipRepository.findByUserId(user.getId()));
    }
}