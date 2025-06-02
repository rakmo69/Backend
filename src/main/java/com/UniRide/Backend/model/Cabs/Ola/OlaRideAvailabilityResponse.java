package com.UniRide.Backend.model.Cabs.Ola;

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
  private List<RideEstimate> rideEstimate;

  @JsonProperty("previous_cancellation_charges")
  private List<PreviousCancellationCharge> previousCancellationCharges;

  @Data
  public static class HotspotZone {
    @JsonProperty("is_hotpot_zone")
    private boolean isHotpotZone;

    private String desc;

    @JsonProperty("default_pickup_point_id")
    private int defaultPickupPointId;

    // Coordinates defining hotspot limits (list of coordinate pairs)
    @JsonProperty("hotspot_boundary")
    private List<List<Double>> hotspotBoundary;

    // List of designated pickup points inside the hotspot
    @JsonProperty("pickup_points")
    private List<PickupPoint> pickupPoints;
  }

  @Data
  public static class PickupPoint {
    private double lat;
    private double lng;
    private String name;
    private int id;
  }

  @Data
  public static class Category {
    private String id;

    @JsonProperty("display_name")
    private String displayName;

    private String currency;

    @JsonProperty("distance_unit")
    private String distanceUnit;

    @JsonProperty("time_unit")
    private String timeUnit;

    private int eta;

    private String distance;

    @JsonProperty("ride_later_enabled")
    private String rideLaterEnabled;  // sometimes provided as a string

    private String image;

    @JsonProperty("hotspot_pickup_points")
    private List<Integer> hotspotPickupPoints;

    @JsonProperty("cancellation_policy")
    private CancellationPolicy cancellationPolicy;

    @JsonProperty("fare_breakup")
    private List<FareBreakup> fareBreakup;

    @JsonProperty("all_cabs")
    private List<AllCab> allCabs;
  }

  @Data
  public static class CancellationPolicy {
    @JsonProperty("cancellation_charge")
    private int cancellationCharge;

    private String currency;

    @JsonProperty("cancellation_charge_applies_after_time")
    private int cancellationChargeAppliesAfterTime;

    @JsonProperty("time_unit")
    private String timeUnit;
  }

  @Data
  public static class FareBreakup {
    private String type;

    @JsonProperty("minimum_distance")
    private int minimumDistance;

    @JsonProperty("minimum_time")
    private int minimumTime;

    @JsonProperty("base_fare")
    private int baseFare;

    @JsonProperty("minimum_fare")
    private int minimumFare;

    @JsonProperty("cost_per_distance")
    private int costPerDistance;

    @JsonProperty("waiting_cost_per_minute")
    private int waitingCostPerMinute;

    @JsonProperty("ride_cost_per_minute")
    private int rideCostPerMinute;

    private List<Object> surcharge; // Adjust type if known

    @JsonProperty("rates_lower_than_usual")
    private boolean ratesLowerThanUsual;

    @JsonProperty("rates_higher_than_usual")
    private boolean ratesHigherThanUsual;
  }

  @Data
  public static class AllCab {
    private double lat;
    private double lng;
    private String id;
    private int bearing;
    private int accuracy;
  }

  @Data
  public static class RideEstimate {
    private String category;
    private double distance;

    @JsonProperty("travel_time_in_minutes")
    private int travelTimeInMinutes;

    @JsonProperty("amount_min")
    private double minAmount;

    @JsonProperty("amount_max")
    private double maxAmount;

    @JsonProperty("booking_fee")
    private int bookingFee;

    @JsonProperty("booking_fee_breakup")
    private List<BookingFeeBreakup> bookingFeeBreakup;

    private Taxes taxes;

    @JsonProperty("hub_charges")
    private HubCharges hubCharges;

    private Discounts discounts;

    private Upfront upfront;
  }

  @Data
  public static class BookingFeeBreakup {
    @JsonProperty("display_text")
    private String displayText;
    private int value;
  }

  @Data
  public static class Taxes {
    @JsonProperty("total_tax")
    private double totalTax;
  }

  @Data
  public static class HubCharges {
    @JsonProperty("total_hub_fee")
    private int totalHubFee;

    @JsonProperty("pickup_hub_fee")
    private int pickupHubFee;

    @JsonProperty("pickup_hub_name")
    private String pickupHubName;
  }

  @Data
  public static class Discounts {
    @JsonProperty("discount_type")
    private String discountType;

    @JsonProperty("discount_code")
    private String discountCode;

    @JsonProperty("discount_mode")
    private String discountMode;

    private int discount;
    private int cashback;

    @JsonProperty("pass_savings")
    private int passSavings;
  }

  @Data
  public static class Upfront {
    private int fare;

    @JsonProperty("fare_id")
    private String fareId;

    @JsonProperty("select_discount")
    private Object selectDiscount; // Can be refined based on API details

    @JsonProperty("is_upfront_applicable")
    private boolean isUpfrontApplicable;
  }

  @Data
  public static class PreviousCancellationCharge {
    private String currency;

    @JsonProperty("booking_id")
    private String bookingId;

    private double amount;
  }
}
