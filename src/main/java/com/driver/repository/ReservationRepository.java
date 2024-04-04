package com.driver.repository;

import com.driver.model.Reservation;
import com.driver.model.Spot;
import com.driver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer>{

    Optional<Reservation> findBySpot(Spot s);

    void deleteAllBySpot(Spot spot);

    List<Reservation> findAllByUser(User user);
}
