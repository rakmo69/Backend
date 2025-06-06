package com.UniRide.Backend.service.Cabs.Rapido;

import com.UniRide.Backend.model.Cabs.Rapido.RapidoFareEstimateResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * RapidoApiService handles Rapido’s “rideAmount” endpoint for fare + ETA.
 */
@Service
public class RapidoService {

  @Value("${rapido.api.base-url}")
  private String rapidoBaseUrl;

  @Value("${rapido.api.service-type}")
  private String serviceType;

  @Value("${rapido.api.customer-id}")
  private String customerId;

  @Value("${rapido.api.bearer-token}")
  private String bearerToken;

  @Value("${rapido.api.device-id}")
  private String deviceId;

  @Value("${rapido.api.appid}")
  private String appId;

  @Value("${rapido.api.appversion}")
  private String appVersion;

  @Value("${rapido.api.host}")
  private String hostHeader;

  private final RestTemplate restTemplate = new RestTemplate();
  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Builds and sends a fare+ETA request to Rapido.
   *
   * @param pickupLat   Pickup latitude
   * @param pickupLng   Pickup longitude
   * @param pickupAddress  Pickup address (string; can be empty if unknown)
   * @param dropLat     Drop latitude
   * @param dropLng     Drop longitude
   * @param dropAddress    Drop address
   * @return RapidoFareEstimateResponse containing timeInMts and quotes
   */
  public RapidoFareEstimateResponse getFareEstimate(
      double pickupLat, double pickupLng, String pickupAddress,
      double dropLat, double dropLng, String dropAddress
  ) throws Exception {
    // 1. Build the URL
    String url = UriComponentsBuilder
        .fromHttpUrl(rapidoBaseUrl + "/om/api/orders/v2/rideAmount")
        .toUriString();

    // 2. Build current timestamp (milliseconds since epoch)
    long currentTimestamp = System.currentTimeMillis();

    // 3. Build JSON payload exactly as Rapido expects
    Map<String, Object> payload = new LinkedHashMap<>();
    Map<String, Object> pickupLocation = new LinkedHashMap<>();
    pickupLocation.put("addressType", "");
    pickupLocation.put("address", pickupAddress != null ? pickupAddress : "");
    pickupLocation.put("lat", pickupLat);
    pickupLocation.put("lng", pickupLng);
    pickupLocation.put("name", ""); // optional

    Map<String, Object> dropLocation = new LinkedHashMap<>();
    dropLocation.put("addressType", "");
    dropLocation.put("address", dropAddress != null ? dropAddress : "");
    dropLocation.put("lat", dropLat);
    dropLocation.put("lng", dropLng);
    dropLocation.put("name", ""); // optional

    payload.put("pickupLocation", pickupLocation);
    payload.put("dropLocation", dropLocation);
    payload.put("serviceType", serviceType);
    payload.put("customer", customerId);
    payload.put("couponCode", "");      // empty if none
    payload.put("paymentType", "paytm"); // default; Rapido supports paytm, cash, etc.

    // 4. Convert payload to JSON string
    String jsonPayload = objectMapper.writeValueAsString(payload);

    // 5. Prepare headers
    HttpHeaders headers = new HttpHeaders();
    headers.set("deviceid", deviceId);
    headers.set("latitude", String.valueOf(pickupLat));
    headers.set("longitude", String.valueOf(pickupLng));
    headers.set("appid", appId);
    headers.set("currentdatetime", // Rapido’s Python code used GMT-based "%Y-%m-%d %H:%M:%S"
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneOffset.UTC)
            .format(java.time.Instant.now())
    );
    headers.set("internet", "0");                        // sample value
    headers.set("appversion", appVersion);               // Rapido app version
    headers.set("Authorization", "Bearer " + bearerToken);
    headers.set("Content-Type", "application/json; charset=UTF-8");
    headers.set("Host", hostHeader);
    headers.set("Connection", "Keep-Alive");
    headers.set("Accept-Encoding", "gzip");
    headers.set("User-Agent", "okhttp/3.6.0");
    headers.set("Cache-Control", "no-cache");

    // 6. POST the request
    HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);
    ResponseEntity<RapidoFareEstimateResponse> response = restTemplate.exchange(
        url,
        HttpMethod.POST,
        requestEntity,
        RapidoFareEstimateResponse.class
    );

    return response.getBody();
  }
}
