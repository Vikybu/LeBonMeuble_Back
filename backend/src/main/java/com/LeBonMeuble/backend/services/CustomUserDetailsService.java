package com.LeBonMeuble.backend.services;

import com.LeBonMeuble.backend.entities.EntityUser;
import com.LeBonMeuble.backend.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        EntityUser user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("Email not found");
        }

        // 🔥 On renvoie directement l'EntityUser
        //    car il implémente déjà UserDetails !
        return user;
    }
}