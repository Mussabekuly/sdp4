package kz.sdp.launch.app;

import kz.sdp.launch.diagnostics.HealthReport;
import kz.sdp.launch.diagnostics.PreLaunchDiagnosticsFacade;
import kz.sdp.launch.facade.LaunchAbortedException;
import kz.sdp.launch.facade.LaunchControlFacade;
import kz.sdp.launch.facade.LaunchReport;
import kz.sdp.launch.facade.LaunchSite;
import kz.sdp.launch.facade.MissionPlan;

import java.util.List;
import java.util.Locale;

/**
 * The client. It talks only to the two facades and never touches a subsystem class.
 * Each mission gets a fresh {@link LaunchControlFacade} because one facade controls one vehicle.
 */
public class MissionControlApp {

    public static void main(String[] args) {
        runHealthCheck(LaunchSite.BAIKONUR);
        runHealthCheck(LaunchSite.KOUROU);

        List<MissionPlan> missions = List.of(
                new MissionPlan("ISS-Resupply", LaunchSite.BAIKONUR, 410, 51.6),
                new MissionPlan("Equatorial-Sat", LaunchSite.KOUROU, 550, 6.0),
                new MissionPlan("Geo-Comsat", LaunchSite.BAIKONUR, 35_786, 51.6),
                new MissionPlan("Polar-Imager", LaunchSite.BAIKONUR, 700, 98.0));

        for (MissionPlan mission : missions) {
            launch(mission);
        }
    }

    private static void runHealthCheck(LaunchSite site) {
        HealthReport report = new PreLaunchDiagnosticsFacade().runHealthCheck(site);
        System.out.println("=== Health check: " + site + " ===");
        report.results().forEach((check, passed) ->
                System.out.println("  " + (passed ? "PASS " : "FAIL ") + check));
        System.out.println("  Overall: " + (report.allSystemsGo() ? "GO" : "NO-GO"));
        System.out.println();
    }

    private static void launch(MissionPlan mission) {
        System.out.println("=== Launching " + mission.missionName() + " ===");
        LaunchControlFacade launchControl = new LaunchControlFacade();
        try {
            LaunchReport report = launchControl.launch(mission);
            report.telemetryLog().forEach(line -> System.out.println("  " + line));
            System.out.printf(Locale.ROOT, "  SUCCESS: delta-v %.2f km/s, propellant %.0f kg%n",
                    report.deltaVKms(), report.propellantKg());
        } catch (LaunchAbortedException aborted) {
            aborted.telemetryLog().forEach(line -> System.out.println("  " + line));
            System.out.println("  " + aborted.getMessage());
        }
        System.out.println();
    }
}
