package kz.sdp.launch.subsystem;

import java.util.Map;

/**
 * Provides launch-site weather and decides whether it is within launch limits.
 * Forecasts are simulated with a fixed table so that the demo is deterministic.
 */
public class WeatherStation {

    private static final String NAME = "WeatherStation";
    private static final double MAX_WIND_SPEED_MS = 15.0;
    private static final double MIN_TEMPERATURE_C = -35.0;

    private static final Map<String, WeatherReport> FORECASTS = Map.of(
            "BAIKONUR", new WeatherReport(6.5, false, -4.0),
            "KOUROU", new WeatherReport(9.0, true, 28.0),
            "CAPE_CANAVERAL", new WeatherReport(18.2, false, 24.0));

    public WeatherReport currentConditions(String siteCode) {
        WeatherReport report = FORECASTS.get(siteCode);
        if (report == null) {
            throw new SubsystemException(NAME, "no weather data for site " + siteCode);
        }
        return report;
    }

    public void assertWithinLaunchLimits(String siteCode) {
        WeatherReport weather = currentConditions(siteCode);
        if (weather.windSpeedMs() > MAX_WIND_SPEED_MS) {
            throw new SubsystemException(NAME, "wind " + weather.windSpeedMs()
                    + " m/s exceeds limit of " + MAX_WIND_SPEED_MS + " m/s");
        }
        if (weather.lightningNearby()) {
            throw new SubsystemException(NAME, "lightning detected near the pad");
        }
        if (weather.temperatureC() < MIN_TEMPERATURE_C) {
            throw new SubsystemException(NAME, "temperature below " + MIN_TEMPERATURE_C + " C");
        }
    }
}
