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

  private static final String P2P = "p2p";

  @Autowired
  public RideService(OlaService olaService) {
    this.olaService = olaService;
  }

  /**
   * Returns all ride options for the given vehicleType from multiple providers.
   * For now, only Ola is integrated.
   */
  public List<RideOption> getAllRideOptions(double pickupLat, double pickupLng, Double dropLat, Double dropLng, String vehicleType) {
    List<RideOption> rideOptions = new ArrayList<>();

    // 1. Fetch ride options from Ola
    rideOptions.addAll(getOlaRideOptions(pickupLat, pickupLng, dropLat, dropLng, vehicleType));

    // 2. (Future) Fetch from Uber
    // rideOptions.addAll(getUberRideOptions(pickupLat, pickupLng, dropLat, dropLng, vehicleType));

    // 3. (Future) Fetch from Rapido
    // rideOptions.addAll(getRapidoRideOptions(pickupLat, pickupLng, dropLat, dropLng, vehicleType));

    return rideOptions;
  }


  /**
   * Fetches ride options from Ola for the given vehicleType.
   */
  private List<RideOption> getOlaRideOptions(double pickupLat, double pickupLng, Double dropLat, Double dropLng, String vehicleType) {
    List<RideOption> olaRideOptions = new ArrayList<>();

    // Call Ola
    OlaRideAvailabilityResponse olaResponse = olaService.getRideAvailability(
        pickupLat,
        pickupLng,
        dropLat,
        dropLng,
        P2P,   // service_type
        vehicleType  // category (e.g. "auto", "mini", etc.)
    );

    if (olaResponse != null && olaResponse.getCategories() != null) {
      for (OlaRideAvailabilityResponse.Category category : olaResponse.getCategories()) {
        // Only process the category if it matches our vehicleType filter
        if (vehicleType.equalsIgnoreCase(category.getId())) {
          double estimatedMinFare = 0.0;
          double estimatedMaxFare = 0.0;

          // Check if ride estimates are available and iterate over them
          if (olaResponse.getRideEstimate() != null && !olaResponse.getRideEstimate().isEmpty()) {
            for (OlaRideAvailabilityResponse.RideEstimate estimate : olaResponse.getRideEstimate()) {
              if (vehicleType.equalsIgnoreCase(estimate.getCategory())) {
                estimatedMinFare = estimate.getMinAmount();
                estimatedMaxFare = estimate.getMaxAmount();
                break; // Found the matching estimate, so exit the loop
              }
            }
          }

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
          olaRideOptions.add(option);
        }
      }
    }

    return olaRideOptions;
  }
}
