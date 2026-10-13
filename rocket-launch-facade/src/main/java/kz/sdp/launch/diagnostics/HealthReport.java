package kz.sdp.launch.diagnostics;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Outcome of a pre-launch health check: one PASS/FAIL line per system. */
public record HealthReport(Map<String, Boolean> results) {

    public HealthReport {
        results = Collections.unmodifiableMap(new LinkedHashMap<>(results));
    }

    public boolean allSystemsGo() {
        return !results.containsValue(false);
    }
}
