package com.UniRide.Backend.model.Cabs.Uber;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class UberTimeEstimatesResponse {
  @JsonProperty("times")
  private List<TimeEstimate> times;

  @Data
  public static class TimeEstimate {
    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("display_name")
    private String displayName;

    private int estimate; // ETA in seconds
  }
}
