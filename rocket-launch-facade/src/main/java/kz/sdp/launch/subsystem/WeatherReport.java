package kz.sdp.launch.subsystem;

/** Snapshot of the weather at a launch site. */
public record WeatherReport(double windSpeedMs, boolean lightningNearby, double temperatureC) {
}
