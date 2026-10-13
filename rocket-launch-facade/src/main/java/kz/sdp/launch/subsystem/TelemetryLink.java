package kz.sdp.launch.subsystem;

import java.util.ArrayList;
import java.util.List;

/** Radio link to mission control that records numbered events of one session. */
public class TelemetryLink {

    private static final String NAME = "TelemetryLink";

    private final List<String> events = new ArrayList<>();
    private String sessionName;
    private int nextSequenceNumber;

    public void openSession(String sessionName) {
        this.sessionName = sessionName;
        this.events.clear();
        this.nextSequenceNumber = 1;
        record("link established");
    }

    public void record(String event) {
        if (sessionName == null) {
            throw new SubsystemException(NAME, "no open session");
        }
        events.add(String.format("[%s #%02d] %s", sessionName, nextSequenceNumber++, event));
    }

    public List<String> closeSession() {
        record("link closed");
        List<String> log = List.copyOf(events);
        sessionName = null;
        return log;
    }

    public boolean hasSignal() {
        return true;
    }
}
