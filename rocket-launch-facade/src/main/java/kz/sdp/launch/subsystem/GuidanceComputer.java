package kz.sdp.launch.subsystem;

/**
 * Computes the trajectory to a circular orbit and flies it after lift-off.
 * Uses a simplified energy model: orbital speed + climb energy + losses - Earth rotation bonus.
 */
public class GuidanceComputer {

    private static final String NAME = "GuidanceComputer";
    private static final double EARTH_MU_KM3_S2 = 398_600.0;
    private static final double EARTH_RADIUS_KM = 6_371.0;
    private static final double EQUATOR_ROTATION_SPEED_KMS = 0.465;
    private static final double GRAVITY_AND_DRAG_LOSSES_KMS = 1.5;
    private static final double MIN_ALTITUDE_KM = 160.0;

    private FlightProfile loadedProfile;
    private boolean autopilotEngaged;

    public FlightProfile computeTrajectory(double siteLatitudeDeg, double targetAltitudeKm, double inclinationDeg) {
        if (targetAltitudeKm < MIN_ALTITUDE_KM) {
            throw new SubsystemException(NAME, "orbit below " + MIN_ALTITUDE_KM + " km decays immediately");
        }
        if (inclinationDeg < Math.abs(siteLatitudeDeg)) {
            throw new SubsystemException(NAME, "inclination " + inclinationDeg
                    + " deg is unreachable from latitude " + siteLatitudeDeg + " deg without a plane change");
        }
        double deltaV = orbitalInsertionDeltaV(targetAltitudeKm) + GRAVITY_AND_DRAG_LOSSES_KMS
                - earthRotationBonus(inclinationDeg);
        loadedProfile = new FlightProfile(targetAltitudeKm, inclinationDeg, deltaV);
        return loadedProfile;
    }

    public void engageAutopilot() {
        if (loadedProfile == null) {
            throw new SubsystemException(NAME, "no trajectory loaded");
        }
        autopilotEngaged = true;
    }

    public boolean isAutopilotEngaged() {
        return autopilotEngaged;
    }

    public void reset() {
        loadedProfile = null;
        autopilotEngaged = false;
    }

    private double orbitalInsertionDeltaV(double altitudeKm) {
        double orbitRadius = EARTH_RADIUS_KM + altitudeKm;
        double circularSpeedSquared = EARTH_MU_KM3_S2 / orbitRadius;
        double climbEnergy = 2 * (EARTH_MU_KM3_S2 / EARTH_RADIUS_KM - EARTH_MU_KM3_S2 / orbitRadius);
        return Math.sqrt(circularSpeedSquared + climbEnergy);
    }

    /** Eastward launches gain the ground speed of the site; the gain shrinks as inclination grows. */
    private double earthRotationBonus(double inclinationDeg) {
        return EQUATOR_ROTATION_SPEED_KMS * Math.cos(Math.toRadians(inclinationDeg));
    }
}
