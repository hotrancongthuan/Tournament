package com.duan2.tournamentTDTU.services;

import com.duan2.tournamentTDTU.models.User;
import com.duan2.tournamentTDTU.repositorys.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public User createUser(User user) {
        User user1 = getUserById(user.getID());
        if(user1 != null){
            return user1;
        }
        return userRepository.save(user);
    }

}