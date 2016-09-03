package pl.marchuck.beaconbuilder.lib;

import pl.marchuck.beaconbuilder.ble.Distance;
import pl.marchuck.beaconbuilder.ble.IBeaconData;
import rx.functions.Action0;

/**
 * @author Lukasz Marczak
 * @since 03.09.16.
 */
public class BeaconAction {
    public final String mac;
    @Distance
    public final int distance;
    public final Action0 actionWhenEnter;

    public BeaconAction(String mac, int distance, Action0 actionWhenEnter) {
        this.mac = mac;
        this.distance = distance;
        this.actionWhenEnter = actionWhenEnter;
    }

    public static BeaconAction end() {
        return new BeaconAction("DONE",0,null);
    }
}
