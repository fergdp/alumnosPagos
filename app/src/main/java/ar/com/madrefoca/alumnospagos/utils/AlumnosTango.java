package ar.com.madrefoca.alumnospagos.utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by fernando on 11/11/17.
 */

public class AlumnosTango extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        AlumnosTango.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return AlumnosTango.context;
    }
}
