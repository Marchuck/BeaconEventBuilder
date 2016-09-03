package pl.marchuck.beaconbuilder.ble;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.polidea.rxandroidble.RxBleClient;

@Deprecated
public class BluetoothService extends Service {
    public static final String TAG = BluetoothService.class.getSimpleName();
    private RxBleClient rxBleClient;
    private final IBinder myBinder = new BtBinder();
    private rx.Subscription scanSubscription;

    @Override
    public IBinder onBind(Intent arg0) {
        return myBinder;
    }

    public class BtBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    public BluetoothService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    /**
     * start scan for beacons
     */
//    public void startScan(Subscriber<MonsterPojo> subscriber, DelayedAction delayedAction) {
//        Log.d(TAG, "startDownload: ");
//        if (rxBleClient == null) rxBleClient = RxBleClient.create(this);
//        App.enableBt(this);
//        scanSubscription = rxBleClient.scanBleDevices()
//                .retry(2)
//                .map(result -> new DistBeacon(result.getBleDevice(),
//                        BleMeasure.calculateAccuracy(result))).subscribeOn(Schedulers.computation())
//
//                .flatMap(distBeacon -> new BeaconMatcher().getRandomizedMonster(distBeacon))
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(subscriber);
//        Observable.timer(delayedAction.getTime(), TimeUnit.SECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(delayedAction, throwable -> {
//                    Log.e(TAG, "error: ", throwable);
//                });
//    }

//    public void startScanIBeacons(Subscriber<DistBeacon> subscriber, DelayedAction delayedAction) {
//        Log.d(TAG, "startDownload: ");
//        if (rxBleClient == null) rxBleClient = RxBleClient.create(this);
//        App.enableBt(this);
//        scanSubscription = rxBleClient.scanBleDevices()
//                .retry(2)
//                .map(BleMeasure::asIBeacon)
//                .subscribeOn(Schedulers.computation())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(subscriber);
//        Observable.timer(delayedAction.getTime(), TimeUnit.SECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(delayedAction, throwable -> {
//                    Log.e(TAG, "error: ", throwable);
//                });
//    }


    public void stopScan() {
        Log.d(TAG, "stopScan: ");
        if (scanSubscription != null) {
            scanSubscription.unsubscribe();
            scanSubscription = null;
        }
    }
}
