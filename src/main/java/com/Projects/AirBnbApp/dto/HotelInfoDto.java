package com.Projects.AirBnbApp.dto;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HotelInfoDto {
    private HotelDto hotelDto;
    private List<RoomDto> rooms;
}
