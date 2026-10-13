package kz.sdp.launch.app;

import kz.sdp.launch.subsystem.FlightProfile;
import kz.sdp.launch.subsystem.FuelSystem;
import kz.sdp.launch.subsystem.GuidanceComputer;

import java.util.Locale;

/**
 * An advanced client that deliberately bypasses the facade. The subsystems do not know the facade
 * exists, so an engineer can still use them directly, here to build a payload-planning table.
 */
public class AdvancedEngineerConsole {

    private static final double BAIKONUR_LATITUDE_DEG = 45.96;
    private static final double ISS_INCLINATION_DEG = 51.6;
    private static final int[] ALTITUDES_KM = {200, 400, 800, 2_000, 20_200, 35_786};

    public static void main(String[] args) {
        GuidanceComputer guidance = new GuidanceComputer();
        FuelSystem fuel = new FuelSystem();

        System.out.println("Altitude km | delta-v km/s | propellant kg");
        for (int altitude : ALTITUDES_KM) {
            FlightProfile profile = guidance.computeTrajectory(BAIKONUR_LATITUDE_DEG, altitude, ISS_INCLINATION_DEG);
            double propellant = fuel.propellantRequiredFor(profile.requiredDeltaVKms());
            System.out.printf(Locale.ROOT, "%11d | %12.2f | %13.0f%n", altitude, profile.requiredDeltaVKms(), propellant);
        }
    }
}
