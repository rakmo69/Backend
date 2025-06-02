package com.UniRide.Backend.model.Cabs.Uber;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class UberPriceEstimatesResponse {
  @JsonProperty("prices")
  private List<PriceEstimate> prices;

  @Data
  public static class PriceEstimate {
    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("display_name")
    private String displayName;

    private double distance;

    @JsonProperty("low_estimate")
    private Double lowEstimate;   // can be null for TAXI (“metered”)

    @JsonProperty("high_estimate")
    private Double highEstimate;  // can be null for TAXI

    private int duration;         // in seconds

    private String currencyCode;

    private String estimate;      // formatted string, e.g. "$13-17"

    @JsonProperty("surge_multiplier")
    private Double surgeMultiplier;
  }
}
