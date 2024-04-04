package com.driver.services.impl;

import com.driver.model.Reservation;
import com.driver.model.Spot;
import com.driver.model.User;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository4;
    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    SpotRepository spotRepository;
    @Override
    public void deleteUser(Integer userId) {

        Optional<User> optionalUser = userRepository4.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Retrieve all reservations associated with the user
            List<Reservation> reservations = reservationRepository.findAllByUser(user);

            // Update spot occupancy status for each reservation
            for (Reservation reservation : reservations) {
                Spot spot = reservation.getSpot();
                spot.setOccupied(false);
                spotRepository.save(spot);
            }

            // Delete all reservations associated with the user
            reservationRepository.deleteAll(reservations);

            // Delete the user
            userRepository4.delete(user);
        }
    }

    @Override
    public User updatePassword(Integer userId, String password) {

        Optional<User> user = userRepository4.findById(userId);
        if(user.isEmpty())
            return null;
        User userdtl = user.get();
        userdtl.setPassword(password);
        return userRepository4.save(userdtl);

    }

    @Override
    public void register(String name, String phoneNumber, String password) {
        User user = new User(name, phoneNumber,password);
        userRepository4.save(user);
    }
}
