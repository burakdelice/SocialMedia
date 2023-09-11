package com.burakdelice.service;

import com.burakdelice.repository.IUserRepository;
import com.burakdelice.repository.entity.UserProfile;
import com.burakdelice.utility.ServiceManager;
import org.springframework.stereotype.Service;

@Service
public class UserService extends ServiceManager<UserProfile, String> {

    private final IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        super(userRepository);
        this.userRepository = userRepository;

    }
}
