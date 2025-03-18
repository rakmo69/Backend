package com.UniRide.Backend.service.Cabs.Ola;

public enum ServiceType {
  P2P("p2p"),
  RENTAL("rental"),
  OUTSTATION("outstation");

  private final String value;

  ServiceType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
