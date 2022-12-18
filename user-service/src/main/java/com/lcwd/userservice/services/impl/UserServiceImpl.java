package com.lcwd.userservice.services.impl;

import com.lcwd.userservice.entities.Hotel;
import com.lcwd.userservice.entities.Rating;
import com.lcwd.userservice.entities.User;
import com.lcwd.userservice.exception.ResourceNotFoundException;
import com.lcwd.userservice.repositories.UserRepository;
import com.lcwd.userservice.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public User saveUser(User user) {

        //generate unique userId

        String randomUserId = UUID.randomUUID().toString();
        user.setUserId(randomUserId);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUser() {
        List<User> users = userRepository.findAll();
        return users;
    }

    @Override
    public User getUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with given id is not found on server!! : " + userId));

        //get rating from RATING-SERVICE
        //http://localhost:8083/ratings/users/8d5fb3db-4c27-410e-8059-6eb175ba83b9
        Rating[] ratings = restTemplate.getForObject("http://RATING-SERVICE/ratings/users/"+user.getUserId(), Rating[].class);
        List<Rating> ratingList = Arrays.asList(ratings);
        logger.info("get rating: {} "+ ratingList);
        ratingList.stream().map(rating -> {
            //get rating from HOTEl-SERVICE
            //http://localhost:8082/hotels/fc388902-c24b-46d9-afbf-b7dbfc47d41b
            logger.info("get hotel id: {} "+ rating.getHotelId());
            ResponseEntity<Hotel> forEntity = restTemplate.getForEntity("http://HOTEL-SERVICE/hotels/" + rating.getHotelId(), Hotel.class);
            logger.info("response status code: {} "+ forEntity.getStatusCode());
            Hotel hotel = forEntity.getBody();
            rating.setHotel(hotel);

            return rating;
        }).collect(Collectors.toList());

        user.setRatings(ratingList);
        return user;
    }
}
