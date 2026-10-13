package kz.sdp.launch.facade;

import kz.sdp.launch.subsystem.EngineController;
import kz.sdp.launch.subsystem.FlightProfile;
import kz.sdp.launch.subsystem.FuelSystem;
import kz.sdp.launch.subsystem.GuidanceComputer;
import kz.sdp.launch.subsystem.LaunchPad;
import kz.sdp.launch.subsystem.SubsystemException;
import kz.sdp.launch.subsystem.TelemetryLink;
import kz.sdp.launch.subsystem.WeatherStation;

import java.util.List;
import java.util.Locale;

/**
 * Facade over the launch subsystems. A client asks for {@link #launch(MissionPlan)} and the facade runs
 * the countdown in the only safe order. If any step fails, it rolls the vehicle back to a safe state.
 * <p>
 * The facade only coordinates: every calculation and safety rule lives in the subsystem classes.
 */
public class LaunchControlFacade {

    private final WeatherStation weatherStation;
    private final GuidanceComputer guidanceComputer;
    private final FuelSystem fuelSystem;
    private final LaunchPad launchPad;
    private final EngineController engineController;
    private final TelemetryLink telemetryLink;

    public LaunchControlFacade() {
        this(new WeatherStation(), new GuidanceComputer(), new FuelSystem(),
                new LaunchPad(), new EngineController(), new TelemetryLink());
    }

    public LaunchControlFacade(WeatherStation weatherStation, GuidanceComputer guidanceComputer,
                               FuelSystem fuelSystem, LaunchPad launchPad,
                               EngineController engineController, TelemetryLink telemetryLink) {
        this.weatherStation = weatherStation;
        this.guidanceComputer = guidanceComputer;
        this.fuelSystem = fuelSystem;
        this.launchPad = launchPad;
        this.engineController = engineController;
        this.telemetryLink = telemetryLink;
    }

    public LaunchReport launch(MissionPlan plan) throws LaunchAbortedException {
        telemetryLink.openSession(plan.missionName());
        try {
            FlightProfile profile = planTrajectory(plan);
            fuelVehicle(profile);
            runFinalCountdown(plan.site());
            liftOff();
            return new LaunchReport(plan.missionName(), profile.requiredDeltaVKms(),
                    fuelSystem.loadedPropellantKg(), telemetryLink.closeSession());
        } catch (SubsystemException failure) {
            List<String> log = abortAndSecureVehicle(failure);
            throw new LaunchAbortedException(plan.missionName(), failure.getMessage(), log, failure);
        }
    }

    private FlightProfile planTrajectory(MissionPlan plan) {
        FlightProfile profile = guidanceComputer.computeTrajectory(
                plan.site().latitudeDeg(), plan.targetAltitudeKm(), plan.inclinationDeg());
        telemetryLink.record(String.format(Locale.ROOT, "trajectory loaded, delta-v %.2f km/s", profile.requiredDeltaVKms()));
        return profile;
    }

    private void fuelVehicle(FlightProfile profile) {
        fuelSystem.loadPropellantFor(profile.requiredDeltaVKms());
        telemetryLink.record(String.format(Locale.ROOT, "propellant loaded: %.0f kg", fuelSystem.loadedPropellantKg()));
        fuelSystem.pressurize();
        telemetryLink.record("tanks pressurised to " + fuelSystem.tankPressureBar() + " bar");
    }

    private void runFinalCountdown(LaunchSite site) {
        launchPad.retractServiceTower();
        telemetryLink.record("T-60s service tower retracted");
        weatherStation.assertWithinLaunchLimits(site.name());
        telemetryLink.record("T-30s weather is GO");
    }

    private void liftOff() {
        engineController.ignite(fuelSystem.tankPressureBar());
        engineController.verifyNominalThrust();
        telemetryLink.record("T-0 ignition, thrust " + engineController.currentThrustKn() + " kN");
        launchPad.releaseHoldDownClamps();
        telemetryLink.record("LIFT-OFF");
        guidanceComputer.engageAutopilot();
        telemetryLink.record("autopilot engaged");
    }

    /** Undoes whatever the countdown managed to do, in reverse order. */
    private List<String> abortAndSecureVehicle(SubsystemException failure) {
        telemetryLink.record("HOLD HOLD HOLD - " + failure.getMessage());
        if (engineController.isRunning()) {
            engineController.shutdown();
            telemetryLink.record("engines shut down");
        }
        if (fuelSystem.isFueled()) {
            fuelSystem.drain();
            telemetryLink.record("propellant drained");
        }
        if (!launchPad.isVehicleReleased()) {
            launchPad.secure();
            telemetryLink.record("pad secured");
        }
        guidanceComputer.reset();
        return telemetryLink.closeSession();
    }
}
