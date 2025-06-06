package com.UniRide.Backend.model.Cabs.Rapido;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Response from Rapido’s POST /om/api/orders/v2/rideAmount (fare+ETA endpoint).
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RapidoFareEstimateResponse {
  @JsonProperty("info")
  private Info info;

  @JsonProperty("data")
  private DataBlock data;

  @JsonIgnoreProperties(ignoreUnknown = true)
  @Data
  public static class Info {
    private String message;
    private String status; // e.g., "success"
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  @Data
  public static class DataBlock {
    @JsonProperty("timeInMts")
    private int timeInMts; // ETA in minutes

    @JsonProperty("quotes")
    private List<Quote> quotes;
    // other fields (like requestId) can be added if needed
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  @Data
  public static class Quote {
    @JsonProperty("serviceId")
    private String serviceId;

    @JsonProperty("amount")
    private double amount;
  }
}
