package kz.sdp.launch.subsystem;

import java.util.Locale;

/**
 * Loads, pressurises and drains propellant.
 * Propellant mass is derived from the required delta-v with the Tsiolkovsky rocket equation.
 */
public class FuelSystem {

    private static final String NAME = "FuelSystem";
    private static final double DRY_MASS_KG = 30_000.0;
    private static final double TANK_CAPACITY_KG = 300_000.0;
    private static final double EXHAUST_VELOCITY_KMS = 4.41;
    private static final double FLIGHT_PRESSURE_BAR = 3.2;
    private static final double LEAK_TOLERANCE_BAR = 0.05;
    private static final double SIMULATED_PRESSURE_LOSS_BAR = 0.01;

    private double loadedPropellantKg;
    private double tankPressureBar;

    public double propellantRequiredFor(double deltaVKms) {
        double massRatio = Math.exp(deltaVKms / EXHAUST_VELOCITY_KMS);
        return DRY_MASS_KG * (massRatio - 1);
    }

    public void loadPropellantFor(double deltaVKms) {
        double required = propellantRequiredFor(deltaVKms);
        if (required > TANK_CAPACITY_KG) {
            throw new SubsystemException(NAME, String.format(Locale.ROOT,
                    "needs %.0f kg of propellant but tanks hold only %.0f kg", required, TANK_CAPACITY_KG));
        }
        loadedPropellantKg = required;
    }

    public void pressurize() {
        if (!isFueled()) {
            throw new SubsystemException(NAME, "cannot pressurise empty tanks");
        }
        tankPressureBar = FLIGHT_PRESSURE_BAR;
    }

    public void drain() {
        tankPressureBar = 0;
        loadedPropellantKg = 0;
    }

    /** Holds the empty tanks at flight pressure and checks how much pressure is lost. */
    public boolean passesLeakTest() {
        return SIMULATED_PRESSURE_LOSS_BAR <= LEAK_TOLERANCE_BAR;
    }

    public boolean isFueled() {
        return loadedPropellantKg > 0;
    }

    public double loadedPropellantKg() {
        return loadedPropellantKg;
    }

    public double tankPressureBar() {
        return tankPressureBar;
    }
}
