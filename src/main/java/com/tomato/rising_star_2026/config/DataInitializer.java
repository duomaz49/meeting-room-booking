package com.tomato.rising_star_2026.config;

import com.tomato.rising_star_2026.model.Room;
import com.tomato.rising_star_2026.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoomRepository roomRepository;

    public DataInitializer(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public void run(String... args) {
        log.info("Initializing sample rooms...");

        List<Room> rooms = List.of(
                new Room("Conference Room A"),
                new Room("Conference Room B"),
                new Room("Meeting Room 1"),
                new Room("Meeting Room 2"),
                new Room("Board Room")
        );

        roomRepository.saveAll(rooms);

        log.info("Created {} sample rooms", rooms.size());
    }
}
