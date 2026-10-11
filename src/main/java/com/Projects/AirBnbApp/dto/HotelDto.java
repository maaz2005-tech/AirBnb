package com.Projects.AirBnbApp.dto;

import com.Projects.AirBnbApp.entity.HotelContactInfo;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class HotelDto {
    private Long id;
    private String name;
    private  String city;
    private String[] photos;
    private String[] amenities;
    private HotelContactInfo contactInfo;
    private Boolean active;
}
/*
{
    "name":"Hotel Lotus",
    "city":"Delhi",
    "contactInfo"={
        "address":"Central Delhi",
        "email":"hello@lotushotels.com",
        "phoneNumber":"9358357395",
        "location":"74.2381.28.43124"
    },
    "amenities":["AC","Lake View","Pool Area"],
    "photos":["https://via.placeholder.com/50"]
}
*/
