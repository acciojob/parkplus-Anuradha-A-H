package com.driver.controllers;

import com.driver.model.Reservation;
import com.driver.services.impl.ReservationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservation")
public class ReservationController {
    @Autowired
    ReservationServiceImpl reservationService;
    @PostMapping("/reserveSpot")
    public Reservation reserveSpot(@RequestParam Integer userId, @RequestParam Integer parkingLotId, @RequestParam Integer timeInHours, @RequestParam Integer numberOfWheels) throws Exception{
        //Reserve a spot in the given parkingLot such that the total price is minimum. Note that the price per hour for each spot is different
        //Note that the vehicle can only be parked in a spot having a type equal to or larger than given vehicle
        //If parkingLot is not found, user is not found, or no spot is available, throw "Cannot make reservation" exception.
        try{
            Reservation r = reservationService.reserveSpot(userId,parkingLotId,timeInHours,numberOfWheels);
            return r;
        }catch (Exception e)
        {
            throw  new Exception(e.getMessage());
        }

    }
//    @GetMapping
//    public List<Reservation> getall(){
//        return reservationService.getall();
//    }

}
