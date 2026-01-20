package com.tomato.rising_star_2026.exception;

public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException() {
        super("Booking not found");
    }
}
