package kz.sdp.launch.subsystem;

/** Controls the service tower and the hold-down clamps that keep the rocket on the pad. */
public class LaunchPad {

    private static final String NAME = "LaunchPad";

    private boolean towerRetracted;
    private boolean clampsReleased;

    public void retractServiceTower() {
        towerRetracted = true;
    }

    public void releaseHoldDownClamps() {
        if (!towerRetracted) {
            throw new SubsystemException(NAME, "service tower is still attached");
        }
        clampsReleased = true;
    }

    /** Returns the pad to a safe state: clamps engaged, tower back in place. */
    public void secure() {
        if (clampsReleased) {
            throw new SubsystemException(NAME, "vehicle has already left the pad");
        }
        towerRetracted = false;
    }

    public boolean isTowerRetracted() {
        return towerRetracted;
    }

    public boolean isVehicleReleased() {
        return clampsReleased;
    }
}
