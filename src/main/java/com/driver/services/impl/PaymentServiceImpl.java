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
    ReservationRepository reservationRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    SpotRepository spotRepository;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {
        Reservation reservation = reservationRepository.findById(reservationId).orElse(null);

        // Check bill Amount
        int billAmount = reservation.getNumberOfHours() * reservation.getSpot().getPricePerHour();
        if(amountSent < billAmount)
            throw new Exception("Insufficient Amount");

        Payment payment = new Payment();

        // Check Payment Mode
        switch (mode.toUpperCase()){
            case ("CARD"):
                payment.setPaymentMode(PaymentMode.CARD);
                break;
            case ("CASH"):
                payment.setPaymentMode(PaymentMode.CASH);
                break;
            case ("UPI"):
                payment.setPaymentMode(PaymentMode.UPI);
                break;
            default:
                throw new Exception("Payment mode not detected");
        }

        payment.setPaymentCompleted(true);
        payment.setReservation(reservation);

        //paymentRepository2.save(payment);
        reservationRepository.save(reservation);

        return payment;
    }
}
