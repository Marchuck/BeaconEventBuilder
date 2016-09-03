package pl.marchuck.beaconbuilder.ble;

import android.support.annotation.Nullable;
import android.util.Log;

import com.polidea.rxandroidble.RxBleScanResult;

import java.util.Arrays;


/**
 * Parses the Manufactured Data field of an iBeacon
 * <p/>
 * The parsing is based on the following schema:
 * <pre>
 * Byte|Value
 * -------------------------------------------------
 * 0	4C - Byte 1 (LSB) of Company identifier code
 * 1	00 - Byte 0 (MSB) of Company identifier code (0x004C == Apple)
 * 2	02 - Byte 0 of iBeacon advertisement indicator
 * 3	15 - Byte 1 of iBeacon advertisement indicator
 * 4	e2 |\
 * 5	c5 |\\
 * 6	6d |#\\
 * 7	b5 |##\\
 * 8	df |###\\
 * 9	fb |####\\
 * 10	48 |#####\\
 * 11	d2 |#####|| iBeacon
 * 12	b0 |#####|| Proximity UUID
 * 13	60 |#####//
 * 14	d0 |####//
 * 15	f5 |###//
 * 16	a7 |##//
 * 17	10 |#//
 * 18	96 |//
 * 19	e0 |/
 * 20	00 - major
 * 21	00
 * 22	00 - minor
 * 23	00
 * 24	c5 - The 2's complement of the calibrated Tx Power
 * </pre>
 *
 * @author Alexandros Schillings
 */

public final class IBeaconData {
    public static final String TAG = IBeaconData.class.getSimpleName();
    private final int mCalibratedTxPower;
    private final int mCompanyIdentidier;
    private final int mIBeaconAdvertisment;
    private final int mMajor;
    private final int mMinor;
    private final String mUUID;
    @Distance
    private final int calculatedDistance;
    private final String mac;

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

    public String getMac() {
        return mac;
    }
    //    /**
//     * Instantiates a new iBeacon manufacturer data object.
//     *
//     * @param device a {@link BluetoothLeDevice}
//     * @throws IllegalArgumentException if the data is not from an iBeacon.
//     */
//    public IBeaconData(final BluetoothLeDevice device) {
//        this(device.getAdRecordStore().getRecord(AdRecord.TYPE_MANUFACTURER_SPECIFIC_DATA).getData());
//    }

    //    /**
//     * Instantiates a new iBeacon manufacturer data object.
//     *
//     * @param manufacturerData the {@link AdRecord#TYPE_MANUFACTURER_SPECIFIC_DATA} data array
//     * @throws IllegalArgumentException if the data is not from an iBeacon.
//     */
    public IBeaconData(final byte[] manufacturerData, int rssi, String mac) {
        this.mac = mac;
        final byte[] intArray = Arrays.copyOfRange(manufacturerData, 0, 2);
        //ByteUtils.invertArray(intArray);

        mCompanyIdentidier = ByteUtils.getIntFrom2ByteArray(intArray);
        if (can(manufacturerData, 4))
            mIBeaconAdvertisment = ByteUtils.getIntFrom2ByteArray(Arrays.copyOfRange(manufacturerData, 2, 4));
        else mIBeaconAdvertisment = 0;

        if (can(manufacturerData, 20))
            mUUID = ByteUtils.calculateUuidString(Arrays.copyOfRange(manufacturerData, 4, 20));
        else mUUID = "";

        if (can(manufacturerData, 22))
            mMajor = ByteUtils.getIntFrom2ByteArray(Arrays.copyOfRange(manufacturerData, 20, 22));
        else mMajor = 0;

        if (can(manufacturerData, 24))
            mMinor = ByteUtils.getIntFrom2ByteArray(Arrays.copyOfRange(manufacturerData, 22, 24));
        else mMinor = 0;

        if (can(manufacturerData, 25)) mCalibratedTxPower = manufacturerData[24];
        else mCalibratedTxPower = 0;
        //Log.i(TAG, "IBeaconData: " + mCalibratedTxPower);

        calculatedDistance = BleMeasure.getDistance(BleMeasure.calculateAccuracy(mCalibratedTxPower, rssi));
    }

    @Distance
    public int getCalculatedDistance() {
        return calculatedDistance;
    }

    private boolean can(byte[] manufacturerData, int hi) {
        return manufacturerData.length >= hi;

    }

    @Override
    public String toString() {
        return "mCalibratedTxPower=" + mCalibratedTxPower +
                ", mCompanyIdentidier=" + mCompanyIdentidier +
                ", mIBeaconAdvertisment=" + mIBeaconAdvertisment +
                ", mMajor=" + mMajor +
                ", mMinor=" + mMinor +
                ", mUUID='" + mUUID;
    }

    /**
     * Gets the calibrated TX power of the iBeacon device as reported.
     *
     * @return the calibrated TX power
     */
    public int getCalibratedTxPower() {
        return mCalibratedTxPower;
    }

    /**
     * Gets the iBeacon company identifier.
     *
     * @return the company identifier
     */
    public int getCompanyIdentifier() {
        return mCompanyIdentidier;
    }

    public int getIBeaconAdvertisement() {
        return mIBeaconAdvertisment;
    }

    /**
     * Gets the iBeacon Major value.
     *
     * @return the Major value
     */
    public int getMajor() {
        return mMajor;
    }

    /**
     * Gets the iBeacon Minor value.
     *
     * @return the Minor value
     */
    public int getMinor() {
        return mMinor;
    }

    /**
     * Gets the iBeacon UUID.
     *
     * @return the UUID
     */
    public String getUUID() {
        return mUUID;
    }

    public String getReadableDistance() {
        return BleMeasure.readableDistance(calculatedDistance);
    }
}

