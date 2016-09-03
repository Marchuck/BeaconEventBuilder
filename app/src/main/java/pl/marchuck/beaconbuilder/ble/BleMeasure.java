package pl.marchuck.beaconbuilder.ble;

import android.support.annotation.Nullable;
import android.util.Log;

import com.polidea.rxandroidble.RxBleScanResult;


/**
 * @author Lukasz Marczak
 * @since 10.07.16.
 */
//todo: refactor and clean to avoid misunderstanding
public class BleMeasure {
    public static final String TAG = BleMeasure.class.getSimpleName();

    @Nullable
    public static IBeaconData asIBeacon(RxBleScanResult result) {
        AdRecordStore adRecordStore = new AdRecordStore(AdRecordUtils.parseScanRecordAsSparseArray(result.getScanRecord()));
        AdRecord record = adRecordStore.getRecord(AdRecord.TYPE_MANUFACTURER_SPECIFIC_DATA);
        if (record == null) {
            return null;
        }
        byte[] data = record.getData();
        return new IBeaconData(data, result.getRssi(), result.getBleDevice().getMacAddress());
    }

    public static double calculateAccuracy(RxBleScanResult result) {
        AdRecordStore adRecordStore = new AdRecordStore(AdRecordUtils.parseScanRecordAsSparseArray(result.getScanRecord()));
        AdRecord record = adRecordStore.getRecord(AdRecord.TYPE_MANUFACTURER_SPECIFIC_DATA);
        if (record == null) {
            Log.e(TAG, "nullable ad record");
            return Distance.NOT_A_BEACON;
        }
        byte[] data = record.getData();
        int rssi = result.getRssi();

        IBeaconData ibeaconData = new IBeaconData(data, rssi, result.getBleDevice().getMacAddress());
        Log.d("BleMeasure", "getDistance: " + ibeaconData.toString());
        int txPower = ibeaconData.getCalibratedTxPower();
        return calculateAccuracy(txPower, rssi);
    }

    @Distance
    public static int getDistance(RxBleScanResult result) {
        double accuracy = calculateAccuracy(result);
        return getDistance(accuracy);
    }


    private static final double DISTANCE_THRESHOLD_WTF = 0.0;
    private static final double DISTANCE_THRESHOLD_IMMEDIATE = 0.5;
    private static final double DISTANCE_THRESHOLD_NEAR = 3.0;

    public static String readableDistance(@Distance int distance) {
        return distance == Distance.NEAR ? "NEAR"
                : distance == Distance.FAR ? "FAR"
                : distance == Distance.IMMEDIATE ? "IMMEDIATE" : "UNKNOWN";
    }


    /**
     * Calculates the accuracy of an RSSI reading.
     * <p/>
     * The code was taken from <a href="http://stackoverflow.com/questions/20416218/understanding-ibeacon-distancing" /a>
     *
     * @param txPower the calibrated TX power of an iBeacon
     * @param rssi    the RSSI value of the iBeacon
     * @return the calculated Accuracy
     */

    public static double calculateAccuracy(final int txPower, final double rssi) {
        //    Log.d(TAG, "calculateAccuracy: " + txPower + ", " + rssi);
        if (rssi == 0 || txPower == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        final double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            return (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
        }
    }

    @Distance
    public static int getDistance(final double accuracy) {
        if (accuracy < DISTANCE_THRESHOLD_WTF) {
            return Distance.UNKNOWN;
        }

        if (accuracy < DISTANCE_THRESHOLD_IMMEDIATE) {
            return Distance.IMMEDIATE;
        }

        if (accuracy < DISTANCE_THRESHOLD_NEAR) {
            return Distance.NEAR;
        }
        return Distance.FAR;
    }
}
