package com.UniRide.Backend.service;

import com.UniRide.Backend.model.Cabs.OlaRideAvailabilityResponse;
import com.UniRide.Backend.model.RideOption;
import com.UniRide.Backend.service.Cabs.OlaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RideService {

  private final OlaService olaService;

  @Autowired
  public RideService(OlaService olaService) {
    this.olaService = olaService;
  }

  /**
   * Returns all ride options for the given vehicleType from multiple providers.
   * For now, only Ola is integrated.
   */
  public List<RideOption> getAllRideOptions(String vehicleType) {
    List<RideOption> rideOptions = new ArrayList<>();

    // Hard-coded pickup for example; in real scenario, pass these from the controller or method params
    double pickupLat = 12.8953741;
    double pickupLng = 77.5859018;

    // Call Ola
    OlaRideAvailabilityResponse olaResponse = olaService.getRideAvailability(
        pickupLat,
        pickupLng,
        null,    // dropLat
        null,    // dropLng
        "p2p",   // service_type
        vehicleType  // category (e.g. "auto", "mini", etc.)
    );

    if (olaResponse != null && olaResponse.getCategories() != null) {
      for (OlaRideAvailabilityResponse.Category category : olaResponse.getCategories()) {
        // Only process the category if it matches our vehicleType filter
        if (vehicleType.equalsIgnoreCase(category.getId())) {
          // For simplicity, we’ll assume we don’t have an explicit fare range
          // If a drop location were provided, we could parse the 'ride_estimate' object
          double estimatedMinFare = olaResponse.getRideEstimate().getMinAmount();
          double estimatedMaxFare = olaResponse.getRideEstimate().getMaxAmount();
          int eta = category.getEta(); // e.g., 1 minute

          // Create a unified RideOption object
          RideOption option = new RideOption(
              "Ola",
              vehicleType,
              estimatedMinFare,
              estimatedMaxFare,
              eta,
              4
          );
          rideOptions.add(option);
        }
      }
    }

    // Here is where you'd also call UberService, RapidoService, etc.

    return rideOptions;
  }
}
