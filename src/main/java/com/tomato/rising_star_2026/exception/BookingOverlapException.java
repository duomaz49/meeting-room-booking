package com.tomato.rising_star_2026.exception;

public class BookingOverlapException extends RuntimeException {

    public BookingOverlapException() {
        super("Booking overlaps with an existing booking for this room");
    }
}
