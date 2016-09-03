package pl.marchuck.beaconbuilder;

/**
 * @author Lukasz Marczak
 * @since 03.09.16.
 */
public class RealBeaconData {
    public final String mac;
    public final String major;
    public final String minor;
    public final String UUID;
    public final String name;
    public final String manufacturer;

    public RealBeaconData(String name, String mac, String major, String minor, String UUID) {
        this.name = name;
        this.mac = mac;
        this.major = major;
        this.minor = minor;
        this.UUID = UUID;
        this.manufacturer = name.startsWith("estimote") ? "estimote" :
                name.startsWith("kontakt") ? "kontakt" :
                        name.startsWith("nordic") ? "nordicSemiconductors" : "unknown_" + name;
    }
}
