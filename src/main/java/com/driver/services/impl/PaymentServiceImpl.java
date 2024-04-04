package com.driver.services.impl;

import com.driver.model.Payment;
import com.driver.model.PaymentMode;
import com.driver.model.Reservation;
import com.driver.model.Spot;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;
    @Autowired
    SpotRepository spotRepository;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {
        Optional<Reservation> reser = reservationRepository2.findById(reservationId);
        if(reser.isEmpty())
        {
             throw new Exception("");
        }
        Reservation reservation = reser.get();

        Spot spot = reservation.getSpot();
        int bill = spot.getPricePerHour() * reservation.getNumberOfHours();

        mode = mode.toLowerCase(); // Convert mode to lowercase for case-insensitive comparison
        PaymentMode p = getPaymentmode(mode);
        if (!mode.equals("cash") && !mode.equals("card") && !mode.equals("upi") ) {
            throw new Exception("Payment mode not detected");
        }
        if(p == null)
        {
            throw new Exception("Payment mode not detected");
        }
        if (amountSent < bill) {
            throw new Exception("Insufficient Amount");
        }

        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setPaymentCompleted(true);
        payment.setPaymentMode(p);
        spot.setOccupied(false);
        spot.getReservationList().removeIf(r -> r.getId() == reservationId);

        spotRepository.save(spot);
        // Save the payment entity
        return paymentRepository2.save(payment);
    }

    public PaymentMode getPaymentmode(String mode)
    {
        if(mode.equals("cash"))
        {
            return PaymentMode.CASH;
        }else if(mode.equals("card"))
        {
            return PaymentMode.CARD;
        }else if(mode.equals("upi"))
        {
            return PaymentMode.UPI;
        }
        return null;
    }
}
