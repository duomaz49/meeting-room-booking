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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private BookingService bookingService;

    private Room testRoom;
    private LocalDateTime futureStart;
    private LocalDateTime futureEnd;

    @BeforeEach
    void setUp() {
        testRoom = new Room(1L, "Test Room");
        futureStart = LocalDateTime.now().plusDays(1);
        futureEnd = LocalDateTime.now().plusDays(1).plusHours(2);
    }

    @Test
    @DisplayName("createBooking - should create booking when all validations pass")
    void createBooking_shouldCreateBookingWhenValid() {
        BookingRequest request = new BookingRequest(1L, "Test User", futureStart, futureEnd);
        Booking savedBooking = new Booking(1L, testRoom, "Test User", futureStart, futureEnd, BookingStatus.BOOKED);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));
        when(bookingRepository.existsOverlappingBooking(1L, futureStart, futureEnd)).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        BookingResponse response = bookingService.createBooking(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getRoomId()).isEqualTo(1L);
        assertThat(response.getStatus()).isEqualTo(BookingStatus.BOOKED);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    @DisplayName("createBooking - should throw RoomNotFoundException when room does not exist")
    void createBooking_shouldThrowWhenRoomNotFound() {
        BookingRequest request = new BookingRequest(999L, "Test User", futureStart, futureEnd);
        when(roomRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(RoomNotFoundException.class)
                .hasMessage("Room not found");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("createBooking - should throw BookingOverlapException when booking overlaps")
    void createBooking_shouldThrowWhenBookingOverlaps() {
        BookingRequest request = new BookingRequest(1L, "Test User", futureStart, futureEnd);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));
        when(bookingRepository.existsOverlappingBooking(1L, futureStart, futureEnd)).thenReturn(true);

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(BookingOverlapException.class)
                .hasMessage("Booking overlaps with an existing booking for this room");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("createBooking - should throw InvalidBookingTimeException when start is after end")
    void createBooking_shouldThrowWhenStartAfterEnd() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        BookingRequest request = new BookingRequest(1L, "Test User", start, end);

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(InvalidBookingTimeException.class)
                .hasMessage("Start time must be before end time");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("createBooking - should throw InvalidBookingTimeException when start equals end")
    void createBooking_shouldThrowWhenStartEqualsEnd() {
        LocalDateTime sameTime = LocalDateTime.now().plusDays(1);
        BookingRequest request = new BookingRequest(1L, "Test User", sameTime, sameTime);

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(InvalidBookingTimeException.class)
                .hasMessage("Start time must be before end time");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("createBooking - should throw InvalidBookingTimeException when booking is in the past")
    void createBooking_shouldThrowWhenBookingInPast() {
        LocalDateTime pastStart = LocalDateTime.now().minusDays(1);
        LocalDateTime pastEnd = LocalDateTime.now().minusHours(1);
        BookingRequest request = new BookingRequest(1L, "Test User", pastStart, pastEnd);

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(InvalidBookingTimeException.class)
                .hasMessage("Booking cannot be in the past");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("cancelBooking - should cancel booking successfully")
    void cancelBooking_shouldCancelBookingSuccessfully() {
        Booking existingBooking = new Booking(1L, testRoom, "Test User", futureStart, futureEnd, BookingStatus.BOOKED);
        Booking canceledBooking = new Booking(1L, testRoom, "Test User", futureStart, futureEnd, BookingStatus.CANCELED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(canceledBooking);

        BookingResponse response = bookingService.cancelBooking(1L);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(BookingStatus.CANCELED);
        verify(bookingRepository).save(existingBooking);
    }

    @Test
    @DisplayName("cancelBooking - should throw BookingNotFoundException when booking does not exist")
    void cancelBooking_shouldThrowWhenBookingNotFound() {
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.cancelBooking(999L))
                .isInstanceOf(BookingNotFoundException.class)
                .hasMessage("Booking not found");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("getBookingsByRoom - should return active bookings for room")
    void getBookingsByRoom_shouldReturnActiveBookings() {
        Booking booking1 = new Booking(1L, testRoom, "Test User", futureStart, futureEnd, BookingStatus.BOOKED);
        Booking booking2 = new Booking(2L, testRoom, "Test User 2", futureStart.plusDays(1), futureEnd.plusDays(1), BookingStatus.BOOKED);

        when(roomRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByRoomIdAndStatus(1L, BookingStatus.BOOKED))
                .thenReturn(List.of(booking1, booking2));

        List<BookingResponse> responses = bookingService.getBookingsByRoom(1L);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getStatus()).isEqualTo(BookingStatus.BOOKED);
        assertThat(responses.get(1).getStatus()).isEqualTo(BookingStatus.BOOKED);
    }

    @Test
    @DisplayName("getBookingsByRoom - should return empty list when no bookings exist")
    void getBookingsByRoom_shouldReturnEmptyListWhenNoBookings() {
        when(roomRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByRoomIdAndStatus(1L, BookingStatus.BOOKED))
                .thenReturn(List.of());

        List<BookingResponse> responses = bookingService.getBookingsByRoom(1L);

        assertThat(responses).isEmpty();
    }

    @Test
    @DisplayName("getBookingsByRoom - should throw RoomNotFoundException when room does not exist")
    void getBookingsByRoom_shouldThrowWhenRoomNotFound() {
        when(roomRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> bookingService.getBookingsByRoom(999L))
                .isInstanceOf(RoomNotFoundException.class)
                .hasMessage("Room not found");

        verify(bookingRepository, never()).findByRoomIdAndStatus(any(), any());
    }
}
