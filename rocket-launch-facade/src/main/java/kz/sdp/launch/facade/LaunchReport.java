package kz.sdp.launch.facade;

import java.util.List;

/** Result of a successful launch, expressed in plain values only. */
public record LaunchReport(String missionName, double deltaVKms, double propellantKg, List<String> telemetryLog) {

    public LaunchReport {
        telemetryLog = List.copyOf(telemetryLog);
    }
}
