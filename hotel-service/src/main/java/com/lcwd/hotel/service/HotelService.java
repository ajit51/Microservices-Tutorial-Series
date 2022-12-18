package com.lcwd.hotel.service;

import java.util.List;

import com.lcwd.hotel.entity.Hotel;

public interface HotelService {
	
	Hotel create(Hotel hotel);
	List<Hotel> getAllHotel();
	Hotel getSingleHotel(String hotelId);
}
