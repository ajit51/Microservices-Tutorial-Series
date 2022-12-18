package com.lcwd.userservice.services;

import com.lcwd.userservice.entities.User;

import java.util.List;

public interface UserService {

    //create user
    User saveUser(User user);

    //get all user
    List<User> getAllUser();

    //get single user of given userID
    User getUser(String userId);
}
