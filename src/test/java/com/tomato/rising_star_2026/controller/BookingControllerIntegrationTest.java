package com.tomato.rising_star_2026.controller;

import com.tomato.rising_star_2026.dto.BookingRequest;
import com.tomato.rising_star_2026.model.Booking;
import com.tomato.rising_star_2026.model.BookingStatus;
import com.tomato.rising_star_2026.model.Room;
import com.tomato.rising_star_2026.repository.BookingRepository;
import com.tomato.rising_star_2026.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private Room testRoom;
    private LocalDateTime futureStart;
    private LocalDateTime futureEnd;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        roomRepository.deleteAll();

        testRoom = roomRepository.save(new Room("Test Room"));
        futureStart = LocalDateTime.now().plusDays(1).withNano(0);
        futureEnd = LocalDateTime.now().plusDays(1).plusHours(2).withNano(0);
    }

    @Test
    @DisplayName("POST /api/bookings - should create booking successfully")
    void createBooking_shouldCreateBookingSuccessfully() throws Exception {
        BookingRequest request = new BookingRequest(testRoom.getId(), futureStart, futureEnd);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomId", is(testRoom.getId().intValue())))
                .andExpect(jsonPath("$.roomName", is("Test Room")))
                .andExpect(jsonPath("$.status", is("BOOKED")));
    }

    @Test
    @DisplayName("POST /api/bookings - should return 404 when room not found")
    void createBooking_shouldReturn404WhenRoomNotFound() throws Exception {
        BookingRequest request = new BookingRequest(999L, futureStart, futureEnd);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Room not found")));
    }

    @Test
    @DisplayName("POST /api/bookings - should return 409 when booking overlaps")
    void createBooking_shouldReturn409WhenBookingOverlaps() throws Exception {
        bookingRepository.save(new Booking(testRoom, futureStart, futureEnd));

        BookingRequest request = new BookingRequest(testRoom.getId(), futureStart, futureEnd);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Booking overlaps with an existing booking for this room")));
    }

    @Test
    @DisplayName("POST /api/bookings - should return 400 when validation fails")
    void createBooking_shouldReturn400WhenValidationFails() throws Exception {
        BookingRequest request = new BookingRequest(null, null, null);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Failed")));
    }

    @Test
    @DisplayName("DELETE /api/bookings/{id} - should cancel booking successfully")
    void cancelBooking_shouldCancelBookingSuccessfully() throws Exception {
        Booking booking = bookingRepository.save(new Booking(testRoom, futureStart, futureEnd));

        mockMvc.perform(delete("/api/bookings/{id}", booking.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CANCELED")));
    }

    @Test
    @DisplayName("DELETE /api/bookings/{id} - should return 404 when booking not found")
    void cancelBooking_shouldReturn404WhenBookingNotFound() throws Exception {
        mockMvc.perform(delete("/api/bookings/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Booking not found")));
    }

    @Test
    @DisplayName("GET /api/rooms/{id}/bookings - should return active bookings")
    void getBookingsByRoom_shouldReturnActiveBookings() throws Exception {
        bookingRepository.save(new Booking(testRoom, futureStart, futureEnd));
        bookingRepository.save(new Booking(testRoom, futureStart.plusDays(1), futureEnd.plusDays(1)));

        Booking canceledBooking = new Booking(testRoom, futureStart.plusDays(2), futureEnd.plusDays(2));
        canceledBooking.setStatus(BookingStatus.CANCELED);
        bookingRepository.save(canceledBooking);

        mockMvc.perform(get("/api/rooms/{id}/bookings", testRoom.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].status", is("BOOKED")))
                .andExpect(jsonPath("$[1].status", is("BOOKED")));
    }

    @Test
    @DisplayName("GET /api/rooms/{id}/bookings - should return empty list when no bookings")
    void getBookingsByRoom_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/rooms/{id}/bookings", testRoom.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/rooms/{id}/bookings - should return 404 when room not found")
    void getBookingsByRoom_shouldReturn404WhenRoomNotFound() throws Exception {
        mockMvc.perform(get("/api/rooms/{id}/bookings", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Room not found")));
    }
}
