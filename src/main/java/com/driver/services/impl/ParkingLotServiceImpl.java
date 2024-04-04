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
    public Spot addSpot(int parkingLotId, Integer numberOfWheels, Integer pricePerHour) {
        Optional<ParkingLot> parkingLotOptional = parkingLotRepository.findById(parkingLotId);
        if (parkingLotOptional.isEmpty()) {
            throw new IllegalArgumentException("Parking lot not found");
        }
        ParkingLot parkingLot = parkingLotOptional.get();
        SpotType spotType = determineSpotType(numberOfWheels);
        Spot spot = new Spot(parkingLot, spotType, pricePerHour, false);
        Spot savedSpot = spotRepository.save(spot);
        parkingLot.getSpotList().add(savedSpot);
        parkingLotRepository.save(parkingLot);
        return savedSpot;
    }

    @Override
    public void deleteSpot(int spotId) {
        Optional<Spot> spotOptional = spotRepository.findById(spotId);
        if (spotOptional.isPresent()) {
            Spot spot = spotOptional.get();
            ParkingLot parkingLot = spot.getParkingLot();
            parkingLot.getSpotList().removeIf(s -> s.getId() == spotId);
            parkingLotRepository.save(parkingLot);
            reservationRepository.deleteAllBySpot(spot);
            spotRepository.delete(spot);
        }
    }

    @Override
    public Spot updateSpot(int parkingLotId, int spotId, int pricePerHour) {
        Optional<Spot> spotOptional = spotRepository.findById(spotId);
        if (spotOptional.isPresent()) {
            Spot spot = spotOptional.get();
            spot.setPricePerHour(pricePerHour);
            return spotRepository.save(spot);
        } else {
            throw new IllegalArgumentException("Spot not found");
        }
    }

    @Override
    public void deleteParkingLot(int parkingLotId) {
        Optional<ParkingLot> parkingOptional = parkingLotRepository.findById(parkingLotId);
        if (parkingOptional.isEmpty()) {
            return; // Parking lot not found, nothing to delete
        }

        ParkingLot parkingLot = parkingOptional.get();
        List<Spot> spots = parkingLot.getSpotList();
        if (spots != null) {
            // Delete all associated spots
            for (Spot spot : spots) {
                // Delete spot reservations (if any)
                reservationRepository.deleteAllBySpot(spot);
                // Delete the spot
                spotRepository.delete(spot);
            }
        }

        // Delete the parking lot entity
        parkingLotRepository.delete(parkingLot);
    }

    private SpotType determineSpotType(Integer numberOfWheels) {
        if (numberOfWheels == 2) {
            return SpotType.TWO_WHEELER;
        } else if (numberOfWheels == 4) {
            return SpotType.FOUR_WHEELER;
        } else {
            return SpotType.OTHERS;
        }
    }
}
