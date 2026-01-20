package com.tomato.rising_star_2026.exception;

public class RoomNotFoundException extends RuntimeException {

    public RoomNotFoundException() {
        super("Room not found");
    }
}
