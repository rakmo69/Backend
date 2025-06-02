package com.UniRide.Backend.model.Cabs.Uber;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class UberProductsResponse {
  @JsonProperty("products")
  private List<Product> products;

  @Data
  public static class Product {
    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("product_group")
    private String productGroup;

    private int capacity;

    @JsonProperty("price_details")
    private PriceDetails priceDetails;

    @JsonProperty("image")
    private String imageUrl;

    @JsonProperty("cash_enabled")
    private boolean cashEnabled;

    @JsonProperty("shared")
    private boolean shared;

    @JsonProperty("description")
    private String description;
    // (Other fields can be added if you need them)
  }

  @Data
  public static class PriceDetails {
    private double base;
    private double minimum;

    @JsonProperty("cost_per_minute")
    private double costPerMinute;

    @JsonProperty("cost_per_distance")
    private double costPerDistance;

    @JsonProperty("distance_unit")
    private String distanceUnit;

    @JsonProperty("cancellation_fee")
    private double cancellationFee;

    @JsonProperty("currency_code")
    private String currencyCode;

    @JsonProperty("service_fees")
    private List<ServiceFee> serviceFees;

    @Data
    public static class ServiceFee {
      private double fee;
      private String name;
    }
  }
}
