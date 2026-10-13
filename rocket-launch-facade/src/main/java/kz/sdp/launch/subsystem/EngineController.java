package kz.sdp.launch.subsystem;

/** Ignites the engines, verifies thrust and shuts them down when needed. */
public class EngineController {

    private static final String NAME = "EngineController";
    private static final double MIN_FEED_PRESSURE_BAR = 3.0;
    private static final double NOMINAL_THRUST_KN = 4_000.0;
    private static final double MIN_THRUST_RATIO = 0.95;

    private boolean running;
    private double currentThrustKn;

    public void ignite(double feedPressureBar) {
        if (feedPressureBar < MIN_FEED_PRESSURE_BAR) {
            throw new SubsystemException(NAME, "feed pressure " + feedPressureBar
                    + " bar is below " + MIN_FEED_PRESSURE_BAR + " bar");
        }
        running = true;
        currentThrustKn = NOMINAL_THRUST_KN;
    }

    public void verifyNominalThrust() {
        if (!running || currentThrustKn < NOMINAL_THRUST_KN * MIN_THRUST_RATIO) {
            throw new SubsystemException(NAME, "thrust " + currentThrustKn + " kN is below nominal");
        }
    }

    public void shutdown() {
        running = false;
        currentThrustKn = 0;
    }

    /** A self-test is only allowed while the engines are cold. */
    public boolean passesSelfTest() {
        return !running;
    }

    public boolean isRunning() {
        return running;
    }

    public double currentThrustKn() {
        return currentThrustKn;
    }
}
