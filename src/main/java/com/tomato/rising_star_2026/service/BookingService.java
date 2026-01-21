package com.tomato.rising_star_2026.service;

import com.tomato.rising_star_2026.dto.BookingRequest;
import com.tomato.rising_star_2026.dto.BookingResponse;
import com.tomato.rising_star_2026.exception.BookingNotFoundException;
import com.tomato.rising_star_2026.exception.BookingOverlapException;
import com.tomato.rising_star_2026.exception.InvalidBookingTimeException;
import com.tomato.rising_star_2026.exception.RoomNotFoundException;
import com.tomato.rising_star_2026.model.Booking;
import com.tomato.rising_star_2026.model.BookingStatus;
import com.tomato.rising_star_2026.model.Room;
import com.tomato.rising_star_2026.repository.BookingRepository;
import com.tomato.rising_star_2026.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    public BookingService(BookingRepository bookingRepository, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
    }

    public BookingResponse createBooking(BookingRequest request) {
        log.info("Creating booking for room {} from {} to {}",
                request.getRoomId(), request.getStartTime(), request.getEndTime());

        validateBookingTime(request.getStartTime(), request.getEndTime());

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(RoomNotFoundException::new);

        if (bookingRepository.existsOverlappingBooking(
                request.getRoomId(), request.getStartTime(), request.getEndTime())) {
            throw new BookingOverlapException();
        }

        Booking booking = new Booking(room, request.getStartTime(), request.getEndTime());
        Booking savedBooking = bookingRepository.save(booking);

        log.info("Booking created with id {}", savedBooking.getId());
        return BookingResponse.fromEntity(savedBooking);
    }

    public BookingResponse cancelBooking(Long bookingId) {
        log.info("Canceling booking with id {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(BookingNotFoundException::new);

        if(booking.getStatus() == BookingStatus.CANCELED) {
            return BookingResponse.fromEntity(booking);
        }

        booking.setStatus(BookingStatus.CANCELED);
        Booking savedBooking = bookingRepository.save(booking);

        log.info("Booking {} canceled", bookingId);
        return BookingResponse.fromEntity(savedBooking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByRoom(Long roomId) {
        log.info("Fetching active bookings for room {}", roomId);

        if (!roomRepository.existsById(roomId)) {
            throw new RoomNotFoundException();
        }

        return bookingRepository.findByRoomIdAndStatus(roomId, BookingStatus.BOOKED)
                .stream()
                .map(BookingResponse::fromEntity)
                .toList();
    }

    private void validateBookingTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
            throw new InvalidBookingTimeException("Start time must be before end time");
        }

        if (startTime.isBefore(LocalDateTime.now())) {
            throw new InvalidBookingTimeException("Booking cannot be in the past");
        }
    }
}
