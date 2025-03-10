package com.UniRide.Backend.model.Cabs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OlaRideAvailabilityResponse {
  @JsonProperty("hotspot_zone")
  private HotspotZone hotspotZone;

  @JsonProperty("categories")
  private List<Category> categories;

  @JsonProperty("ride_estimate")
  private RideEstimate rideEstimate;

  @JsonProperty("previous_cancellation_charges")
  private List<PreviousCancellationCharge> previousCancellationCharges;

  @Data
  public static class HotspotZone {
    @JsonProperty("is_hotpot_zone")
    private boolean isHotspotZone;

    private String desc;

    @JsonProperty("default_pickup_point_id")
    private int defaultPickupPointId;

    // You can add fields like hotspot_boundary, pickup_points, etc.
  }

  @Data
  public static class Category {
    private String id;
    private String displayName;
    private int eta;

    // e.g. "cancellation_policy", "fare_breakup", "all_cabs"
    // Add as needed
  }

  @Data
  public static class RideEstimate {
    private String category;
    private double distance;
    private int travelTimeInMinutes;

    @JsonProperty("amount_min")
    private double minAmount;

    @JsonProperty("amount_max")
    private double maxAmount;

    // e.g. booking_fee, taxes, discounts, upfront, etc.
  }

  @Data
  public static class PreviousCancellationCharge {
    private String currency;
    @JsonProperty("booking_id")
    private String bookingId;
    private double amount;
  }
}
