package com.Projects.AirBnbApp.service;

import com.Projects.AirBnbApp.entity.Booking;

public interface CheckoutService {
    String getCheckoutSession(Booking booking, String successUrl, String failureUrl);
}
