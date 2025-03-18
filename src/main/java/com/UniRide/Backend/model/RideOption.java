package com.UniRide.Backend.model;

import com.UniRide.Backend.service.Cabs.ServiceProvider;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RideOption {
  private ServiceProvider serviceProvider;
  private String vehicleType;
  private double minPrice;
  private double maxPrice;
  private int etaMinutes;
  private int capacity;
}
