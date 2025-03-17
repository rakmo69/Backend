package com.UniRide.Backend.service.Cabs;

public enum ServiceProvider {
  OLA("Ola"),
  UBER("Uber"),
  RAPIDO("Rapido");

  private final String displayName;

  ServiceProvider(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
