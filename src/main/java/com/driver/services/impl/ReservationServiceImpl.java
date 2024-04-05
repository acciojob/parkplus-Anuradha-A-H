package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        try {
            Reservation reservation = new Reservation();
            reservation.setNumberOfHours(timeInHours);
            User user = userRepository3.findById(userId).get();
            ParkingLot parkingLot = parkingLotRepository3.findById(parkingLotId).get();
            Spot spot1 = getSpot(numberOfWheels, parkingLot);
            reservation.setSpot(spot1);
            reservation.setUser(user);
            user.getReservationList().add(reservation);
            spot1.getReservationList().add(reservation);
            spotRepository3.save(spot1);
            //reservationRepository3.save(reservation);
            userRepository3.save(user);
            return reservation;
        }
        catch(Exception e){
            return null;
        }
    }

    private static Spot getSpot(Integer numberOfWheels, ParkingLot parkingLot) throws Exception {
        List<Spot> spotList = parkingLot.getSpotList();
        Spot spot1 = null;
        int price = Integer.MAX_VALUE;
        for (Spot spot : spotList) {
            int wheel = 0;
            if (spot.getSpotType() == SpotType.TWO_WHEELER) {
                wheel = 2;
            } else if (spot.getSpotType() == SpotType.FOUR_WHEELER) {
                wheel = 4;
            } else if (spot.getSpotType() == SpotType.OTHERS) {
                wheel = 99;
            }
            if (spot.getOccupied() == false && wheel > numberOfWheels && spot.getPricePerHour() < price) {
                spot1 = spot;
                price = spot.getPricePerHour();
            }
        }
        if (spot1 == null) {
            throw new Exception();
        }
        spot1.setOccupied(true);
        return spot1;
    }


}
