package duck.switches.android.service;

// The steps of entering the DIY Mode and connecting to an existing WiFi network:
//
//    1. Entering the Compatible Pairing Mode (AP) by long press the paring button for 5 seconds
//       after power on
//    2. Connecting the Access Point named ITEAD-XXXXXXXXXX with default password 12345678 via
//       mobile phone or PC
//    3. Browser visits http://10.10.7.1/
//    4. Filling in the existing WiFi network SSID and password
//    5. Entering DIY Mode successfully with specific WiFi network connected.
//
// Example for Single Channel DIY Plug (BASICR3, RFR3, MINI) enters DIY Mode:
//
//    1. Power on;
//    2. Long press the button for 5 seconds to enter Compatible Pairing Mode (AP)
//       User tips: If the device has been paired with eWeLink APP, reset the device is necessary
//       by long press the pairing button for 5 seconds, then press another 5 seconds for entering
//       Compatible Pairing Mode (AP)
//    3. The LED indicator will blink continuously
//    4. From mobile phone or PC WiFi setting, an Access Point of the device named ITEAD-XXXXXXXXXX
//       will be found, connect it with default password 12345678
//    5. Open the browser and access http://10.10.7.1/
//    6. Next, fill in WiFi SSID and password that the device would have connected with
//    7. Succeed, now the device is in DIY Mode.
//
// BASICR3/RFR3/MINI HTTP API
//
//    http://developers.sonoff.tech/basicr3-rfr3-mini-http-api.html
//

import android.util.Log;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import duck.switches.android.model.Callback;
import duck.switches.android.model.DeviceInfoMessage;
import duck.switches.android.model.PowerOnState;
import duck.switches.android.model.Seq;
import duck.switches.android.model.StartupMessage;
import duck.switches.android.model.Switch;
import duck.switches.android.model.SwitchInfo;
import duck.switches.android.model.SwitchInfoResult;
import duck.switches.android.model.SwitchListener;
import duck.switches.android.model.SwitchMessage;
import duck.switches.android.model.SwitchState;

public class SwitchService {

    private Timer timer;
    private final List<SwitchListener> listeners = new ArrayList<>();

    public void setSwitchState(Switch aSwitch, SwitchState state, Callback<Boolean> callback) {
        Thread thread = new Thread(() -> {
            String address = aSwitch.getLocalAddress();
            String path = SwitchMessage.path;
            SwitchMessage.Request request = new SwitchMessage.Request();
            request.data.aSwtich = state.toString().toLowerCase(Locale.ROOT);

            HttpService.post(address, path, request, SwitchMessage.Response.class,
                    switchState -> {
                        if (switchState != null && switchState.error == 0) {
                            callback.execute(true);
                        } else {
                            callback.execute(false);
                            String message = switchState == null
                                    ? "unknown error"
                                    : String.valueOf(switchState.error);
                            Log.d(getTag("setSwitchState"), message);
                        }
                    }, error -> {
                        callback.execute(false);
                        Log.d(getTag("setSwitchState"), error.getMessage(), error);
                    });
        });
        thread.setName(getTag("setSwitchState"));
        thread.start();
    }

    public void setPowerOnState(Switch aSwitch, PowerOnState state, Callback<Boolean> callback) {
        Thread thread = new Thread(() -> {
            String address = aSwitch.getLocalAddress();
            String path = StartupMessage.path;
            StartupMessage.Request request = new StartupMessage.Request();
            request.data.startup = state.toString().toLowerCase(Locale.ROOT);

            HttpService.post(address, path, request, StartupMessage.Response.class,
                    startupState -> {
                        if (startupState != null && startupState.error == 0) {
                            callback.execute(true);
                        } else {
                            callback.execute(false);
                            String message = startupState == null
                                    ? "unknown error"
                                    : String.valueOf(startupState.error);
                            Log.d(getTag("setPowerOnState"), message);
                        }
                    }, error -> {
                        callback.execute(false);
                        Log.d(getTag("setPowerOnState"), error.getMessage(), error);
                    });
        });
        thread.setName(getTag("setPowerOnState"));
        thread.start();
    }

    public void getInfo(Switch aSwitch, Callback<SwitchInfoResult> callback) {
        Thread thread = new Thread(() -> {
            getInfo(aSwitch, callback, new Seq(-1));
        });
        thread.setName(getTag("getInfo"));
        thread.start();
    }

    private void getInfo(Switch aSwitch, Callback<SwitchInfoResult> callback, Seq seq) {
        String address = aSwitch.getLocalAddress();
        String path = DeviceInfoMessage.path;
        DeviceInfoMessage.Request request = new DeviceInfoMessage.Request();

        HttpService.post(address, path, request, DeviceInfoMessage.Response.class,
                deviceInfo -> {
                    if (deviceInfo != null && deviceInfo.error == 0 && deviceInfo.seq != seq.value) {
                        DeviceInfoMessage.Response.Data data = deviceInfo.data;
                        String deviceId = data.deviceid;
                        String ssid = data.ssid;
                        String fwVersion = data.fwVersion;
                        SwitchState switchSate = SwitchState.valueOf(data.aSwitch.toUpperCase(Locale.ROOT));
                        PowerOnState powerOnState = PowerOnState.valueOf(data.startup.toUpperCase(Locale.ROOT));
                        SwitchInfo switchInfo = new SwitchInfo(address, deviceId, ssid, fwVersion, switchSate, powerOnState);

                        callback.execute(new SwitchInfoResult(aSwitch, switchInfo));
                        seq.value = deviceInfo.seq;
                    }
                }, error -> {
                    callback.execute(null);
                    if (!(error instanceof UnknownHostException)) {
                        Log.d(getTag("getInfo"), error.getMessage(), error);
                    }
                });
    }

    public void addListener(SwitchListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SwitchListener listener) {
        listeners.remove(listener);
    }

    public void removeAllListeners() {
        listeners.clear();
    }

    public void startListening() {
        stopListening();
        timer = new Timer(getTag("startListening"));
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ArrayList<SwitchListener> listenersCopy = new ArrayList<>(listeners);
                for (SwitchListener listener : listenersCopy) {
                    getInfo(listener.getSwitch(), listener.getCallback(), listener.getSeq());
                }
            }
        }, 0, 10000);
    }

    public void stopListening() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private static String getTag(String name) {
        return SwitchService.class.getSimpleName() + "." + name;
    }
}
