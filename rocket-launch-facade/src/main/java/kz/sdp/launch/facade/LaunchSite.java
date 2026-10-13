package kz.sdp.launch.facade;

/** Launch sites known to mission control, with the latitude that limits reachable orbits. */
public enum LaunchSite {
    BAIKONUR(45.96),
    KOUROU(5.23),
    CAPE_CANAVERAL(28.57);

    private final double latitudeDeg;

    LaunchSite(double latitudeDeg) {
        this.latitudeDeg = latitudeDeg;
    }

    public double latitudeDeg() {
        return latitudeDeg;
    }
}
