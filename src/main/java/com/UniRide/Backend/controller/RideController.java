package com.UniRide.Backend.controller;

import com.UniRide.Backend.model.RideOption;
import com.UniRide.Backend.service.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ride")
public class RideController {
  private final RideService rideService;

  @Autowired
  public RideController(RideService rideService) {
    this.rideService = rideService;
  }

  /**
   * GET /api/ride/options?vehicleType=auto
   *
   * Returns all ride options for the given vehicle type (e.g. "auto", "mini").
   */
  @GetMapping("/options")
  public List<RideOption> getRideOptions(@RequestParam String vehicleType) {
    return rideService.getAllRideOptions(vehicleType);
  }
}
