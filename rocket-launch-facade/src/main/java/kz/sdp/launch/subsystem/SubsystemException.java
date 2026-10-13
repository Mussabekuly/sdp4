package kz.sdp.launch.subsystem;

/**
 * Thrown by any launch subsystem when it cannot complete an operation safely.
 * Carries the name of the failing subsystem so the caller can report it.
 */
public class SubsystemException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String subsystemName;

    public SubsystemException(String subsystemName, String message) {
        super(subsystemName + ": " + message);
        this.subsystemName = subsystemName;
    }

    public String subsystemName() {
        return subsystemName;
    }
}
