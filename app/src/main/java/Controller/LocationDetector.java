package Controller;

import android.util.Log;

/**
 * Created by My PC on 05/12/2015.
 */
class LocationDetector {
    private static LocationDetector instance = null;
    private LocationDetector() {super(); }
    public synchronized static LocationDetector inst(){
        if (instance == null) {
            instance = new LocationDetector();
            Log.d("LocationDetector", "Create new instance");
        }
        return instance;
    }
}
