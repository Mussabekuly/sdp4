package kz.sdp.launch.facade;

import java.util.List;

/**
 * Signals that a launch was scrubbed and the vehicle was returned to a safe state.
 * Hides which subsystem type failed; the client only gets a readable reason and the telemetry log.
 */
public class LaunchAbortedException extends Exception {

    private static final long serialVersionUID = 1L;

    private final transient List<String> telemetryLog;

    LaunchAbortedException(String missionName, String reason, List<String> telemetryLog, Throwable cause) {
        super("Mission " + missionName + " aborted: " + reason, cause);
        this.telemetryLog = List.copyOf(telemetryLog);
    }

    public List<String> telemetryLog() {
        return telemetryLog;
    }
}
