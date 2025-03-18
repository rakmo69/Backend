package com.UniRide.Backend.controller;

import com.UniRide.Backend.model.RideOption;
import com.UniRide.Backend.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ride")
@RequiredArgsConstructor
public class RideController {
  private final RideService rideService;

  /**
   * GET /api/ride/options?pickupLat=12.8953741&pickupLng=77.5859018&dropLat=12.9560643&dropLng=77.6514801&vehicleType=auto
   *
   * Returns all ride options for the given vehicle type and location parameters.
   */
  @GetMapping("/options")
  public List<RideOption> getRideOptions(
      @RequestParam double pickupLat,
      @RequestParam double pickupLng,
      @RequestParam(required = false) Double dropLat,
      @RequestParam(required = false) Double dropLng,
      @RequestParam String vehicleType) {
    return rideService.getAllRideOptions(pickupLat, pickupLng, dropLat, dropLng, vehicleType);
  }
}
