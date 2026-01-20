package com.tomato.rising_star_2026.dto;

import com.tomato.rising_star_2026.model.Booking;
import com.tomato.rising_star_2026.model.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private Long id;
    private Long roomId;
    private String roomName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BookingStatus status;

    public static BookingResponse fromEntity(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getRoom().getId(),
                booking.getRoom().getName(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getStatus()
        );
    }
}
