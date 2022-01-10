package duck.switches.android.phone.ui;

import android.os.Handler;
import android.os.Looper;

public class ThreadUtils {
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    public static void runOnUiThread(final Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            HANDLER.post(runnable);
        }
    }
}
