package pl.marchuck.beaconbuilder.ble;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Lukasz Marczak
 * @since 03.09.16.
 */
@IntDef({Distance.FAR, Distance.NEAR, Distance.IMMEDIATE, Distance.UNKNOWN, Distance.TOO_SHORT_DATA, Distance.NOT_A_BEACON})
@Retention(RetentionPolicy.SOURCE)
public @interface Distance {
    int NOT_A_BEACON = 0x06;
    int TOO_SHORT_DATA = 0x05;
    int UNKNOWN = 0x04;
    int FAR = 0x03;
    int NEAR = 0x02;
    int IMMEDIATE = 0x01;
}
