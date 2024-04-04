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

@@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    SpotRepository spotRepository;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {
        Optional<Reservation> reservationOptional = reservationRepository.findById(reservationId);
        if (reservationOptional.isEmpty()) {
            throw new IllegalArgumentException("Reservation not found");
        }
        Reservation reservation = reservationOptional.get();

        Spot spot = reservation.getSpot();
        int bill = spot.getPricePerHour() * reservation.getNumberOfHours();

        PaymentMode paymentMode = getPaymentMode(mode.toLowerCase());
        if (paymentMode == null) {
            throw new IllegalArgumentException("Invalid payment mode");
        }

        if (amountSent < bill) {
            throw new IllegalArgumentException("Insufficient amount");
        }

        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setPaymentCompleted(true);
        payment.setPaymentMode(paymentMode);

        spot.setOccupied(false);
        spot.getReservationList().removeIf(r -> r.getId() == reservationId);

        spotRepository.save(spot);
        return paymentRepository.save(payment);
    }

    private PaymentMode getPaymentMode(String mode) {
        switch (mode) {
            case "cash":
                return PaymentMode.CASH;
            case "card":
                return PaymentMode.CARD;
            case "upi":
                return PaymentMode.UPI;
            default:
                return null;
        }
    }
}
