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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

@Service
public class ParkingLotServiceImpl implements ParkingLotService {
    @Autowired
    ParkingLotRepository parkingLotRepository1;

    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    SpotRepository spotRepository1;
    @Override
    public ParkingLot addParkingLot(String name, String address) {

        ParkingLot parkingLot = new ParkingLot(name, address);
        return parkingLotRepository1.save(parkingLot);



    }

    @Override
    public Spot addSpot(int parkingLotId, Integer numberOfWheels, Integer pricePerHour) {
        Optional<ParkingLot> parking = parkingLotRepository1.findById(parkingLotId);
        if(parking.isEmpty())
            return null;
        ParkingLot parkingLot = parking.get();
        SpotType spotType = determineSpotType(numberOfWheels);

        Spot spot = new Spot( parkingLot , spotType, pricePerHour, false);

        Spot spot1 = spotRepository1.save(spot);
        List<Spot> spotList = parkingLot.getSpotList();
        spotList.add(spot);
        parkingLot.setSpotList(spotList);
        parkingLotRepository1.save(parkingLot);
        return spot1;
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
    @Override
    public void deleteSpot(int spotId) {
        Optional<Spot> sp = spotRepository1.findById(spotId);
        if(sp.isEmpty())
            return;
        Spot spot = sp.get();

        // Delete the spot entity from the database
        ParkingLot p = spot.getParkingLot();
        List<Spot> spotList = new ArrayList<>(p.getSpotList());
        spotList.removeIf(s -> s.getId() == spotId);
        p.setSpotList(spotList);
        parkingLotRepository1.save(p);


        Optional<Reservation> reservation = reservationRepository.findBySpot(spot);
        reservation.ifPresent(reservationRepository::delete);


        spotRepository1.delete(spot);

    }

    @Override
    public Spot updateSpot(int parkingLotId, int spotId, int pricePerHour) {
        Optional<ParkingLot> parking = parkingLotRepository1.findById(parkingLotId);
        if(parking.isEmpty())
             return null;
        ParkingLot parkingLot = parking.get();
        Optional<Spot> sp = spotRepository1.findById(spotId);
        if(sp.isEmpty())
            return null;
        Spot spot = sp.get();
        spot.setPricePerHour(pricePerHour);


        // Update the spot within the parking lot's list of spots
        List<Spot> spots = parkingLot.getSpotList();
        for (Spot s : spots) {
            if (s.getId() == spotId) {
                s.setPricePerHour(pricePerHour);
                break;
            }
        }
        parkingLot.setSpotList(spots);

        // Save the updated parking lot entity to the database
        ParkingLot updatedParkingLot = parkingLotRepository1.save(parkingLot);

        return spotRepository1.save(spot);


    }

    @Override
    public void deleteParkingLot(int parkingLotId) {
        Optional<ParkingLot> parking = parkingLotRepository1.findById(parkingLotId);
        if(parking.isEmpty())
            return;
        ParkingLot parkingLot = parking.get();

        // Delete all associated spots
        List<Spot> spots = parkingLot.getSpotList();
        for (Spot spot : spots) {
            // Delete spot reservations (if any)
            reservationRepository.deleteAllBySpot(spot);
            // Delete the spot
            spotRepository1.delete(spot);
        }

        // Delete the parking lot entity
        parkingLotRepository1.delete(parkingLot);
    }
}
