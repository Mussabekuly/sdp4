package kz.sdp.launch.diagnostics;

import kz.sdp.launch.facade.LaunchSite;
import kz.sdp.launch.subsystem.EngineController;
import kz.sdp.launch.subsystem.FuelSystem;
import kz.sdp.launch.subsystem.SubsystemException;
import kz.sdp.launch.subsystem.TelemetryLink;
import kz.sdp.launch.subsystem.WeatherStation;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Second, independent facade for ground engineers. It only runs non-destructive checks and never fuels
 * or ignites anything, so the launch API stays small and the diagnostics API stays separate.
 */
public class PreLaunchDiagnosticsFacade {

    private final WeatherStation weatherStation;
    private final FuelSystem fuelSystem;
    private final EngineController engineController;
    private final TelemetryLink telemetryLink;

    public PreLaunchDiagnosticsFacade() {
        this(new WeatherStation(), new FuelSystem(), new EngineController(), new TelemetryLink());
    }

    public PreLaunchDiagnosticsFacade(WeatherStation weatherStation, FuelSystem fuelSystem,
                                      EngineController engineController, TelemetryLink telemetryLink) {
        this.weatherStation = weatherStation;
        this.fuelSystem = fuelSystem;
        this.engineController = engineController;
        this.telemetryLink = telemetryLink;
    }

    public HealthReport runHealthCheck(LaunchSite site) {
        Map<String, Boolean> results = new LinkedHashMap<>();
        results.put("Telemetry signal", telemetryLink.hasSignal());
        results.put("Tank leak test", fuelSystem.passesLeakTest());
        results.put("Engine self-test", engineController.passesSelfTest());
        results.put("Weather at " + site, isWeatherAcceptable(site));
        return new HealthReport(results);
    }

    private boolean isWeatherAcceptable(LaunchSite site) {
        try {
            weatherStation.assertWithinLaunchLimits(site.name());
            return true;
        } catch (SubsystemException outOfLimits) {
            return false;
        }
    }
}
