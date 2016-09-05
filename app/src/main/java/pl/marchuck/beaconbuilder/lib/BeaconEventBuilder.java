package pl.marchuck.beaconbuilder.lib;

import android.content.Context;
import android.util.Log;

import com.polidea.rxandroidble.RxBleClient;
import com.polidea.rxandroidble.RxBleScanResult;
import com.polidea.rxandroidble.internal.RxBleLog;

import java.util.ArrayList;
import java.util.List;

import pl.marchuck.beaconbuilder.BeaconRepository;
import pl.marchuck.beaconbuilder.ble.IBeaconData;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Lukasz Marczak
 * @since 03.09.16.
 */
public class BeaconEventBuilder {

    public static final String TAG = BeaconEventBuilder.class.getSimpleName();

    private final List<BeaconAction> actions = new ArrayList<>();

    private Action0 startAction = new Action0() {
        @Override
        public void call() {
        }
    };
    private Action0 onEndAction = new Action0() {
        @Override
        public void call() {
        }
    };
    private Action1<Throwable> onErrAction = new Action1<Throwable>() {
        @Override
        public void call(Throwable o) {
        }
    };

    public BeaconEventBuilder add(BeaconAction beaconAction) {
        actions.add(beaconAction);
        return this;
    }

    public BeaconEventBuilder doOnStart(Action0 startAction) {
        this.startAction = startAction;
        return this;
    }

    public BeaconEventBuilder doOnEnd(Action0 action) {
        onEndAction = action;
        return this;
    }

    public BeaconEventBuilder doOnError(Action1<Throwable> actionErr) {
        onErrAction = actionErr;
        return this;
    }

//    private AtomicBoolean atomicBoolean = new AtomicBoolean(false);

    public void build(Context context) {
        RxBleClient client = RxBleClient.create(context);
        RxBleClient.setLogLevel(RxBleLog.NONE);
        client.scanBleDevices()
                .doOnSubscribe(startAction)
                .retry(2)
                .map(new Func1<RxBleScanResult, IBeaconData>() {
                    @Override
                    public IBeaconData call(RxBleScanResult result) {
                        return IBeaconData.asIBeacon(result);
                    }
                })
//                .map(new Func1<IBeaconData, BeaconAction>() {
//                    @Override
//                    public BeaconAction call(IBeaconData iBeaconData) {
//                        if (atomicBoolean.get()) return null;
//                        atomicBoolean.set(true);
//                        synchronized (actions) {
//                            Log.d(TAG, "new beacon is: " + iBeaconData.getReadableDistance()
//                                    + " :" + iBeaconData.getMac());
//                            if (actions.isEmpty()) {
//                                atomicBoolean.set(false);
//                                return BeaconAction.end();
//                            }
//                            Log.d(TAG, "current queue: " + printQueue(actions));
//                            int index = indexOfNextBeacon(iBeaconData);
//                            if (index != 0) {
//                                Log.e(TAG, "index: " + index + ", expected: " + 0);
//                                atomicBoolean.set(false);
//                                return null;
//                            }
//                            BeaconAction action = actions.get(index);
////                            if (iBeaconData.getCalculatedDistance() > action.distance) {
////                                return null;
////                            }
//                            actions.remove(action);
//                            Log.e(TAG, "ATOMIC INTEGER VALUE: ");
//                            atomicBoolean.set(false);
//
//                            return action;
//                        }
//                    }
//                })
                //.throttleFirst(1000, TimeUnit.MILLISECONDS)// NOT USE THIS!
                .subscribeOn(Schedulers.trampoline())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(onEndAction)
                .doOnError(onErrAction)
                .subscribe(new Subscriber<IBeaconData>() {
                    @Override
                    public void onStart() {
                        request(1);
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getCause(), e);
                    }

                    @Override
                    public void onNext(IBeaconData iBeaconData) {
                        if (iBeaconData == null) {
                            request(1);
                            return;
                        }

                        BeaconAction beaconAction = null;
                        synchronized (actions) {

                            Log.d(TAG, "new beacon is: " + iBeaconData.getReadableDistance() + " :" + iBeaconData.getMac());
                            if (actions.isEmpty()) {
                                onCompleted();
                                return;
                            }
                            Log.d(TAG, "current queue: " + printQueue(actions));
                            int index = indexOfNextBeacon(iBeaconData);
                            if (index == -1) {
                                request(1);
                                return;
                            }
                            BeaconAction action = actions.get(index);
//                            if (iBeaconData.getCalculatedDistance() > action.distance) {
//                                return null;
//                            }
                            actions.remove(action);
                            Log.e(TAG, "ATOMIC INTEGER VALUE: ");

                            beaconAction = action;
                        }
                        if (beaconAction == null) return;

                        Log.d(TAG, "onNext: " + beaconAction.mac);

                        if (beaconAction.mac.equalsIgnoreCase("DONE")) {
                            Log.e(TAG, "onNext: DONE");
                            onCompleted();
                        } else if (beaconAction.actionWhenEnter != null) {
                            Log.e(TAG, "onNext: ACTION CALLED!");
                            beaconAction.actionWhenEnter.call();
                        }
                        request(1);
                    }
                });
    }

    private String printQueue(List<BeaconAction> actions) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (BeaconAction ba : actions) {
            sb.append(BeaconRepository.findNameFor(ba.mac));
            sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private int indexOfNextBeacon(IBeaconData iBeaconData) {
        for (int i = 0; i < actions.size(); i++) {
            if (actions.get(i).mac.toLowerCase().equals(iBeaconData.getMac().toLowerCase())) {
                return i;
            }
        }
        return -1;
    }

    public static BeaconEventBuilder create() {
        return new BeaconEventBuilder();
    }
}
