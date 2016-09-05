package pl.marchuck.beaconbuilder;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import pl.marchuck.beaconbuilder.ble.Distance;
import pl.marchuck.beaconbuilder.lib.BeaconAction;
import pl.marchuck.beaconbuilder.lib.BeaconEventBuilder;
import rx.functions.Action0;
import rx.functions.Action1;

public class MainActivity extends FragmentActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private TextView t1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert false;
        setContentView(R.layout.activity_main);
        t1 = (TextView) findViewById(R.id.a1);

        BluetoothAdapter.getDefaultAdapter().enable();

        BeaconEventBuilder.create()
                .doOnStart(new Action0() {
                    @Override
                    public void call() {

                        updateText("started discovery...");
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                        updateText("an error occurred: " + throwable.getMessage());
                    }
                })
                .add(new BeaconAction(BeaconRepository.estimoteCyan.mac, Distance.NEAR, new Action0() {
                    @Override
                    public void call() {

                        updateText("cyan is close");
                    }
                }))
                .add(new BeaconAction(BeaconRepository.estimoteCobalt.mac, Distance.NEAR, new Action0() {
                    @Override
                    public void call() {

                        updateText("cobalt near me");
                    }
                }))
                .add(new BeaconAction(BeaconRepository.nordic.mac, Distance.NEAR, new Action0() {
                    @Override
                    public void call() {

                        updateText("I feel nordic");
                    }
                }))
                .doOnEnd(new Action0() {
                    @Override
                    public void call() {

                        updateText("all beacons detected");

                    }
                })
                .build((this));
    }

    private void updateText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String message = t1.getText() + "\n" + text;
                t1.setText(message);
            }
        });
    }
}

