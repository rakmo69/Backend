package com.UniRide.Backend.service;

import com.UniRide.Backend.model.Cabs.Ola.OlaRideAvailabilityResponse;
import com.UniRide.Backend.model.Cabs.Rapido.RapidoFareEstimateResponse;
import com.UniRide.Backend.model.Cabs.Uber.UberPriceEstimatesResponse;
import com.UniRide.Backend.model.Cabs.Uber.UberProductsResponse;
import com.UniRide.Backend.model.Cabs.Uber.UberTimeEstimatesResponse;
import com.UniRide.Backend.model.RideOption;
import com.UniRide.Backend.service.Cabs.Ola.OlaService;
import com.UniRide.Backend.service.Cabs.Ola.ServiceType;
import com.UniRide.Backend.service.Cabs.Rapido.RapidoService;
import com.UniRide.Backend.service.Cabs.Uber.UberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.UniRide.Backend.service.Cabs.ServiceProvider.OLA;
import static com.UniRide.Backend.service.Cabs.ServiceProvider.RAPIDO;
import static com.UniRide.Backend.service.Cabs.ServiceProvider.UBER;

@Service
public class RideService {

  private final OlaService olaService;
  private final UberService uberService;
  private final RapidoService rapidoService;

  @Autowired
  public RideService(OlaService olaService) {
    this.olaService = olaService;
    this.uberService = uberService;
    this.rapidoService = rapidoService;
  }

  /**
   * Returns all ride options for the given vehicleType from multiple providers.
   * For now, only Ola is integrated.
   */
  public List<RideOption> getAllRideOptions(double pickupLat, double pickupLng, Double dropLat, Double dropLng, String vehicleType) {
    List<RideOption> rideOptions = new ArrayList<>();

    // 1. Fetch ride options from Ola
    rideOptions.addAll(getOlaRideOptions(pickupLat, pickupLng, dropLat, dropLng, vehicleType));

    // 2. Fetch ride options from Uber
    rideOptions.addAll(getUberRideOptions(pickupLat, pickupLng, dropLat, dropLng, vehicleType));

    // 3. (Future) Fetch from Rapido
    try {
      rideOptions.addAll(getRapidoRideOptions(pickupLat, pickupLng, dropLat, dropLng));
    } catch (Exception e) {
      // Log the exception in real code. For now, we’ll skip Rapido if there’s an error.
      e.printStackTrace();
    }

    return rideOptions;
  }


  /**
   * Fetches ride options from Ola for the given vehicleType and coordinates.
   */
  private List<RideOption> getOlaRideOptions(double pickupLat, double pickupLng, Double dropLat, Double dropLng, String vehicleType) {
    List<RideOption> olaRideOptions = new ArrayList<>();

    // Call Ola
    OlaRideAvailabilityResponse olaResponse = olaService.getRideAvailability(
        pickupLat,
        pickupLng,
        dropLat,
        dropLng,
        ServiceType.P2P,   // service_type
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

          int eta = category.getEta();

          // Create a unified RideOption object
          RideOption option = new RideOption(
              OLA,
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

  /**
   * Fetches ride options from Uber for the given vehicleType and coordinates.
   */
  private List<RideOption> getUberRideOptions(
      double pickupLat, double pickupLng,
      Double dropLat, Double dropLng,
      String vehicleType
  ) {
    List<RideOption> uberRideOptions = new ArrayList<>();

    // 1. Get available products at pickup location
    List<UberProductsResponse.Product> products = Optional.ofNullable(
        uberService.getProducts(pickupLat, pickupLng)
    ).map(UberProductsResponse::getProducts).orElse(Collections.emptyList());

    // 2. If we have a drop location, get price estimates
    Map<String, UberPriceEstimatesResponse.PriceEstimate> priceMap = new HashMap<>();
    if (dropLat != null && dropLng != null) {
      List<UberPriceEstimatesResponse.PriceEstimate> priceEstimates = Optional.ofNullable(
          uberService.getPriceEstimates(pickupLat, pickupLng, dropLat, dropLng)
      ).map(UberPriceEstimatesResponse::getPrices).orElse(Collections.emptyList());

      for (UberPriceEstimatesResponse.PriceEstimate pe : priceEstimates) {
        priceMap.put(pe.getProductId(), pe);
      }
    }

    // 3. Always fetch time estimates (ETA) at pickup
    Map<String, UberTimeEstimatesResponse.TimeEstimate> timeMap = new HashMap<>();
    List<UberTimeEstimatesResponse.TimeEstimate> timeEstimates = Optional.ofNullable(
        uberService.getTimeEstimates(pickupLat, pickupLng)
    ).map(UberTimeEstimatesResponse::getTimes).orElse(Collections.emptyList());

    for (UberTimeEstimatesResponse.TimeEstimate te : timeEstimates) {
      timeMap.put(te.getProductId(), te);
    }

    // 4. Iterate products, pick those matching vehicleType, build RideOption
    for (UberProductsResponse.Product product : products) {
      // Assume vehicleType corresponds to product_group (e.g., "uberx", "uberblack")
      if (vehicleType.equalsIgnoreCase(product.getProductGroup())) {
        // (a) ETA in seconds → convert to minutes (round up)
        int etaMinutes = 0;
        UberTimeEstimatesResponse.TimeEstimate te = timeMap.get(product.getProductId());
        if (te != null) {
          etaMinutes = (int) Math.ceil(te.getEstimate() / 60.0);
        }

        // (b) Price range
        double lowFare = 0.0, highFare = 0.0;
        if (dropLat != null && dropLng != null) {
          UberPriceEstimatesResponse.PriceEstimate pe = priceMap.get(product.getProductId());
          if (pe != null) {
            lowFare = pe.getLowEstimate() != null ? pe.getLowEstimate() : 0.0;
            highFare = pe.getHighEstimate() != null ? pe.getHighEstimate() : 0.0;
          }
        }

        // (c) Capacity
        int capacity = product.getCapacity();

        // (d) Build a unified RideOption
        RideOption option = new RideOption(
            UBER,
            vehicleType,
            lowFare,
            highFare,
            etaMinutes,
            capacity
        );
        uberRideOptions.add(option);
      }
    }

    return uberRideOptions;
  }


  /**
   * Fetches ride options from Rapido for the given coordinates.
   * vehicleType is not used here since Rapido’s vehicle type is bike for now.
   * Rapido’s model only returns a list of quotes (each with serviceId & amount) plus an ETA.
   */
  private List<RideOption> getRapidoRideOptions(
      double pickupLat, double pickupLng,
      Double dropLat, Double dropLng
  ) throws Exception {
    List<RideOption> rapidoRideOptions = new ArrayList<>();

    // If dropLat/dropLng are missing, Rapido’s endpoint won’t give fare estimates → return empty
    if (dropLat == null || dropLng == null) {
      return rapidoRideOptions;
    }

    // For “address” fields, we’ll just pass empty strings (Rapido doesn’t strictly require a human-readable address)
    String pickupAddress = "";
    String dropAddress = "";

    // 1. Call Rapido’s fare + ETA endpoint
    RapidoFareEstimateResponse response = rapidoService.getFareEstimate(
        pickupLat, pickupLng, pickupAddress,
        dropLat, dropLng, dropAddress
    );

    if (response != null
        && response.getInfo() != null
        && "success".equalsIgnoreCase(response.getInfo().getStatus())
        && response.getData() != null) {

      int etaMinutes = response.getData().getTimeInMts();  // ETA in minutes

      List<RapidoFareEstimateResponse.Quote> quotes = response.getData().getQuotes();
      if (quotes != null) {
        for (RapidoFareEstimateResponse.Quote quote : quotes) {
          // Each quote has a serviceId and amount. Usually serviceId encodes a Rapido sub-service (e.g. “bike” or “auto”).
          String rapidoServiceId = quote.getServiceId();
          double amount = quote.getAmount();

          // We’ll treat Rapido as “vehicleType = BIKE” for simplicity.
          // If you have multiple serviceIds (e.g. “BIKE”, “AUTO”), you can differentiate by serviceId.
          String vehicle = "bike";
          //TODO: If Rapido supports multiple vehicle types in the future, we can map serviceId to vehicleType.

          // Build unified RideOption:
          RideOption option = new RideOption(
              RAPIDO,
              vehicle,
              amount,      // minFare
              amount,      // maxFare (no range provided)
              etaMinutes,  // ETA
              1            // Rapido bike capacity is always 1
          );
          rapidoRideOptions.add(option);
        }
      }
    }

    return rapidoRideOptions;
  }
}
