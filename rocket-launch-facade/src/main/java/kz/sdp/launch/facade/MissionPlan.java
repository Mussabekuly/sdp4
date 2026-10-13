package kz.sdp.launch.facade;

import java.util.Objects;

/** Everything a client has to know to order a launch: a name, a site and a target orbit. */
public record MissionPlan(String missionName, LaunchSite site, double targetAltitudeKm, double inclinationDeg) {

    public MissionPlan {
        Objects.requireNonNull(site, "site");
        if (missionName == null || missionName.isBlank()) {
            throw new IllegalArgumentException("mission name must not be blank");
        }
        if (targetAltitudeKm <= 0) {
            throw new IllegalArgumentException("target altitude must be positive");
        }
        if (inclinationDeg < 0 || inclinationDeg > 180) {
            throw new IllegalArgumentException("inclination must be between 0 and 180 degrees");
        }
    }
}
