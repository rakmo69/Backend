package com.UniRide.Backend.service.Cabs.Uber;


import com.UniRide.Backend.model.Cabs.Uber.UberPriceEstimatesResponse;
import com.UniRide.Backend.model.Cabs.Uber.UberProductsResponse;
import com.UniRide.Backend.model.Cabs.Uber.UberTimeEstimatesResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class UberService {

  @Value("${uber.api.base-url}")
  private String uberBaseUrl;

  @Value("${uber.api.server-token}")
  private String serverToken;

  private final RestTemplate restTemplate = new RestTemplate();

  /**
   * GET /v1.2/products?latitude={lat}&longitude={lng}
   */
  public UberProductsResponse getProducts(double latitude, double longitude) {
    String url = UriComponentsBuilder
        .fromHttpUrl(uberBaseUrl + "/v1.2/products")
        .queryParam("latitude", latitude)
        .queryParam("longitude", longitude)
        .toUriString();

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Token " + serverToken);
    headers.set("Accept-Language", "en_US");
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

    ResponseEntity<UberProductsResponse> response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        requestEntity,
        UberProductsResponse.class
    );

    return response.getBody();
  }

  /**
   * GET /v1.2/estimates/price?start_latitude={}&start_longitude={}&end_latitude={}&end_longitude={}
   */
  public UberPriceEstimatesResponse getPriceEstimates(
      double startLat, double startLng,
      double endLat, double endLng
  ) {
    String url = UriComponentsBuilder
        .fromHttpUrl(uberBaseUrl + "/v1.2/estimates/price")
        .queryParam("start_latitude", startLat)
        .queryParam("start_longitude", startLng)
        .queryParam("end_latitude", endLat)
        .queryParam("end_longitude", endLng)
        .toUriString();

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Token " + serverToken);
    headers.set("Accept-Language", "en_US");
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

    ResponseEntity<UberPriceEstimatesResponse> response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        requestEntity,
        UberPriceEstimatesResponse.class
    );

    return response.getBody();
  }

  /**
   * GET /v1.2/estimates/time?start_latitude={}&start_longitude={}
   */
  public UberTimeEstimatesResponse getTimeEstimates(double startLat, double startLng) {
    String url = UriComponentsBuilder
        .fromHttpUrl(uberBaseUrl + "/v1.2/estimates/time")
        .queryParam("start_latitude", startLat)
        .queryParam("start_longitude", startLng)
        .toUriString();

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Token " + serverToken);
    headers.set("Accept-Language", "en_US");
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

    ResponseEntity<UberTimeEstimatesResponse> response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        requestEntity,
        UberTimeEstimatesResponse.class
    );

    return response.getBody();
  }
}
