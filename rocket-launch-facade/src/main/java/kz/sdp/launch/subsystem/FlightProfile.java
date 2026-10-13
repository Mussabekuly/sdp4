package kz.sdp.launch.subsystem;

/** Trajectory computed by the guidance computer for a specific target orbit. */
public record FlightProfile(double targetAltitudeKm, double inclinationDeg, double requiredDeltaVKms) {
}
