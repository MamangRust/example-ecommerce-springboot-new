package com.sanedge.ecommerce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.models.User;
import com.sanedge.ecommerce.repository.user.UserQueryRepository;
import com.sanedge.ecommerce.security.UserDetailsImpl;

import jakarta.transaction.Transactional;

@Service
public class UserDetailImplService implements UserDetailsService {
    @Autowired
    UserQueryRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return UserDetailsImpl.build(user);
    }
}