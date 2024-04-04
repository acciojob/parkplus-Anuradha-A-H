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
        User user = userRepository3.findById(userId)
                .orElseThrow(() -> new Exception("Cannot make reservation"));

        // Retrieve the parking lot entity from the database
        ParkingLot parkingLot = parkingLotRepository3.findById(parkingLotId)
                .orElseThrow(() -> new Exception("Cannot make reservation"));

        List<Spot> availableSpots = spotRepository3.findByParkingLotAndSpotTypeGreaterThanEqualAndOccupiedFalse(parkingLot, determineSpotType(numberOfWheels));

        if (availableSpots.isEmpty()) {
            throw new Exception("Cannot make reservation");
        }

        Spot minPriceSpot = availableSpots.get(0);
        int minTotalPrice = minPriceSpot.getPricePerHour() * timeInHours;
        for (Spot spot : availableSpots) {
            int totalPrice = spot.getPricePerHour() * timeInHours;
            if (totalPrice < minTotalPrice) {
                minTotalPrice = totalPrice;
                minPriceSpot = spot;
            }
        }


        // Create a new reservation entity for the user and selected spot
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setSpot(minPriceSpot);
        reservation.setNumberOfHours(timeInHours);

        // Update the occupancy status of the selected spot
        minPriceSpot.setOccupied(true);


        // Save the reservation entity
        Reservation re =  reservationRepository3.save(reservation);
        List<Reservation> reservationList = minPriceSpot.getReservationList();
           reservationList.add(re);
           minPriceSpot.setReservationList(reservationList);
        spotRepository3.save(minPriceSpot);
        return re;
    }

    private SpotType determineSpotType(Integer numberOfWheels) {
        // Determine the spot type based on the number of wheels
        if (numberOfWheels == 2) {
            return SpotType.TWO_WHEELER;
        } else if (numberOfWheels == 4) {
            return SpotType.FOUR_WHEELER;
        } else {
            return SpotType.OTHERS;
        }
    }

    public List<Reservation> getall(){
        return reservationRepository3.findAll();
    }
}
