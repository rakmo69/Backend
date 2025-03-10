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
  // For example, https://devapi.olacabs.com
  @Value("${ola.api.base-url}")
  private String olaBaseUrl;

  // The mandatory header for partner token
  @Value("${ola.api.x-app-token}")
  private String xAppToken;

  // If you have a bearer token for the user (optional)
  @Value("${ola.api.bearer-token}")
  private String bearerToken;

  public OlaRideAvailabilityResponse getRideAvailability(
      double pickupLat,
      double pickupLng,
      Double dropLat,         // use Double so it's optional
      Double dropLng,
      String serviceType,
      String category
  ) {
    // Build the URL with query parameters
    UriComponentsBuilder builder = UriComponentsBuilder
        .fromHttpUrl(olaBaseUrl + "/v1/products")
        .queryParam("pickup_lat", pickupLat)
        .queryParam("pickup_lng", pickupLng);

    // Only add drop params if provided
    if (dropLat != null && dropLng != null) {
      builder.queryParam("drop_lat", dropLat)
          .queryParam("drop_lng", dropLng);
    }

    // service_type could be "p2p", "rental", or "outstation"
    if (serviceType != null) {
      builder.queryParam("service_type", serviceType);
    }

    // category could be "auto", "micro", "mini", etc.
    if (category != null) {
      builder.queryParam("category", category);
    }

    String url = builder.toUriString();

    // Set headers
    HttpHeaders headers = new HttpHeaders();
    headers.set("x-app-token", xAppToken);
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
