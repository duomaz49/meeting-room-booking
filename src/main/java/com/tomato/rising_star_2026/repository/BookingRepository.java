package com.tomato.rising_star_2026.repository;

import com.tomato.rising_star_2026.model.Booking;
import com.tomato.rising_star_2026.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByRoomIdAndStatus(Long roomId, BookingStatus status);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
           "WHERE b.room.id = :roomId " +
           "AND b.status = 'BOOKED' " +
           "AND b.startTime < :endTime " +
           "AND b.endTime > :startTime")
    boolean existsOverlappingBooking(
            @Param("roomId") Long roomId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}
