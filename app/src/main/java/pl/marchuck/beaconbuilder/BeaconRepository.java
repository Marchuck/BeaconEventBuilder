package pl.marchuck.beaconbuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lukasz Marczak
 * @since 03.09.16.
 */
public class BeaconRepository {

    public static final RealBeaconData nordic = new RealBeaconData(
            "nordicSemiconductors",
            "C9:C1:FA:BD:69:D5",
            "64189",
            "27093",
            "01122334-4556-6778-899A-ABBCCDDEEFF0".toLowerCase());
    public static final RealBeaconData estimoteStickerBike = new RealBeaconData(
            "estimoteStickerBike",
            "DA:58:32:2D:BB:FC",
            "55410",
            "48605",
            "d0d3fa86-ca76-45ec-9bd9-6af403aeca4b");

    public static final RealBeaconData estimoteStickerChair = new RealBeaconData(
            "estimoteStickerChair",
            "C3:4D:61:8F:85:9D",
            "19119",
            "59569",
            "d0d3fa86-ca76-45ec-9bd9-6af403aeca4b");

    public static final RealBeaconData estimoteStickerCouch = new RealBeaconData(
            "estimoteStickerChair",
            "C6:46:F7:09:20:BA",
            "54",
            "25467",
            "d0d3fa86-ca76-45ec-9bd9-6af403aeca4b");

    public static final RealBeaconData estimoteCyan = new RealBeaconData(
            "estimoteCyan",
            "CA:3D:D8:90:68:6B",
            "26731",
            "55440",
            "b9407f30-f5f8-466e-aff9-25556b57fe6d"
    );

    public static final RealBeaconData estimoteCobalt = new RealBeaconData(
            "estimoteCobalt",
            "C8:E3:A0:43:CC:C3",
            "52419",
            "41027",
            "b9407f30-f5f8-466e-aff9-25556b57fe6d"
    );

    public static final RealBeaconData kontaktBlack = new RealBeaconData(
            "kontaktBlack",
            "FF:B2:B0:F2:64:14",
            "51873",
            "33303",
            "f7826da6-4fa2-4e98-8024-bc5b71e0893e");
    public static final RealBeaconData kontaktWhite = new RealBeaconData(
            "kontaktWhite",
            "E3:0B:F4:01:D0:88",
            "20941",
            "50090",
            "f7826da6-4fa2-4e98-8024-bc5b71e0893e");


    public final List<RealBeaconData> myBeacons = new ArrayList<>();

    public BeaconRepository() {
        myBeacons.add(nordic);
        myBeacons.add(estimoteCobalt);
        myBeacons.add(estimoteCyan);
        myBeacons.add(kontaktBlack);
        myBeacons.add(kontaktWhite);
    }

    public static String findNameFor(String mac) {
        for (RealBeaconData realBeaconData : new BeaconRepository().myBeacons) {
            if (realBeaconData.mac.equalsIgnoreCase(mac)) return realBeaconData.name;
        }
        return "?";
    }
}
