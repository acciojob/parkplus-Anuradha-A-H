package com.driver.services.impl;

import com.driver.model.ParkingLot;
import com.driver.model.Reservation;
import com.driver.model.Spot;
import com.driver.model.SpotType;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.services.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingLotServiceImpl implements ParkingLotService {

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private SpotRepository spotRepository;

    @Override
    public ParkingLot addParkingLot(String name, String address) {
        ParkingLot parkingLot = new ParkingLot(name, address);
        return parkingLotRepository.save(parkingLot);
    }

    @Override
    public Spot addSpot(int parkingLotId, Integer numberOfWheels, Integer pricePerHour){
        ParkingLot parkingLot = parkingLotRepository.findById(parkingLotId).orElse(null);

        Spot spot = new Spot();
        spot.setPricePerHour(pricePerHour);
        spot.setParkingLot(parkingLot);
        // Set the Spot Type
        if (numberOfWheels <= 2) {
            spot.setSpotType(SpotType.TWO_WHEELER);
        } else if (numberOfWheels <= 4) {
            spot.setSpotType(SpotType.FOUR_WHEELER);
        } else {
            spot.setSpotType(SpotType.OTHERS);
        }


        //spotRepository1.save(spot); // Save to Database

        parkingLot.getSpotList().add(spot);
        parkingLotRepository.save(parkingLot); // Save to Database

        return spot;
    }

    @Override
    @Transactional // Add this annotation to ensure the method executes within a transactional context
    public void deleteSpot(int spotId) {
        Spot spot = spotRepository.findById(spotId).orElse(null);

        if(spot != null){
            ParkingLot parkingLot = parkingLotRepository.findById(spot.getParkingLot().getId())
                    .orElse(null);


            parkingLot.getSpotList().remove(spot);
        }
        spotRepository.deleteById(spotId);
    }

    @Override
    public Spot updateSpot(int parkingLotId, int spotId, int pricePerHour) {
        ParkingLot parkingLot = parkingLotRepository.findById(parkingLotId).orElse(null);

        for(Spot spot : parkingLot.getSpotList()){
            if(spot.getId() == spotId){
                spot.setPricePerHour(pricePerHour);
                spotRepository.save(spot);
                return spot;
            }
        }

        return null; // Spot not found
    }

    @Override
    public void deleteParkingLot(int parkingLotId) {
        ParkingLot parkingLot = parkingLotRepository.findById(parkingLotId).orElse(null);

        if(parkingLot != null){
            parkingLot.getSpotList().forEach(spot -> spotRepository.deleteById(spot.getId()));
        }

        parkingLotRepository.deleteById(parkingLotId);
    }


}
