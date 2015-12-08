package Controller;

import android.util.Log;

/**
 * Created by My PC on 04/12/2015.
 */
class LocalDB {
    private static LocalDB instance = null;
    private LocalDB() {super(); }
    public synchronized static LocalDB inst(){
        if (instance == null) {
            instance = new LocalDB();
            Log.d("LocalDB", "Create new instance");
        }
        return instance;
    }
}
