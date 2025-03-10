package com.UniRide.Backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RideOption {
  private String serviceProvider;
  private String vehicleType;
  private double minPrice;
  private double maxPrice;
  private int etaMinutes;
  private int capacity;
}
