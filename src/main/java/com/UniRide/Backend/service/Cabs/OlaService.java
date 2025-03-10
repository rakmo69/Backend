package com.UniRide.Backend.service.Cabs;

import com.UniRide.Backend.model.Cabs.OlaRideAvailabilityResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class OlaService {
  // The Ola Base URL
  @Value("${ola.api.base-url}")
  private String olaBaseUrl;

  // The mandatory header for partner token
  @Value("${ola.api.x-app-token}")
  private String xAppToken;

  // If you have a bearer token for the user (optional)
  @Value("${ola.api.bearer-token}")
  private String bearerToken;

  private static final String PICKUP_LATITUDE = "pickup_lat";
  private static final String PICKUP_LONGITUDE = "pickup_lng";
  private static final String DROP_LATITUDE = "drop_lat";
  private static final String DROP_LONGITUDE = "drop_lng";
  private static final String SERVICE_TYPE = "service_type";
  private static final String CATEGORY = "category";
  private static final String X_APP_TOKEN = "x-app-token";


  public OlaRideAvailabilityResponse getRideAvailability(
      double pickupLatitude,
      double pickupLongitude,
      Double dropLatitude,         // use Double so it's optional
      Double dropLongitude,
      String serviceType,
      String category
  ) {
    // Build the URL with query parameters
    UriComponentsBuilder builder = UriComponentsBuilder
        .fromHttpUrl(olaBaseUrl + "/v1/products")
        .queryParam(PICKUP_LATITUDE, pickupLatitude)
        .queryParam(PICKUP_LONGITUDE, pickupLongitude);

    // Only add drop params if provided
    if (dropLatitude != null && dropLongitude != null) {
      builder.queryParam(DROP_LATITUDE, dropLatitude)
          .queryParam(DROP_LONGITUDE, dropLongitude);
    }

    // service_type could be "p2p", "rental", or "outstation"
    if (serviceType != null) {
      builder.queryParam(SERVICE_TYPE, serviceType);
    }

    // category could be "auto", "micro", "mini", etc.
    if (category != null) {
      builder.queryParam(CATEGORY, category);
    }

    String url = builder.toUriString();

    // Set headers
    HttpHeaders headers = new HttpHeaders();
    headers.set(X_APP_TOKEN, xAppToken);
    // If you want to send a bearer token for user-specific data
    if (bearerToken != null && !bearerToken.isEmpty()) {
      headers.setBearerAuth(bearerToken);
    }

    // Create the request entity
    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

    // Make the GET request
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<OlaRideAvailabilityResponse> response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        requestEntity,
        OlaRideAvailabilityResponse.class
    );

    return response.getBody();
  }
}
