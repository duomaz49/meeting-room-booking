package com.tomato.rising_star_2026.dto;

import com.tomato.rising_star_2026.model.Room;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {

    private Long id;
    private String name;

    public static RoomResponse fromEntity(Room room) {
        return new RoomResponse(room.getId(), room.getName());
    }
}
