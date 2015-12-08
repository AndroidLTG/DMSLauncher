package Controller;

import android.util.Log;

/**
 * Created by My PC on 04/12/2015.
 */
class NetworkTransaction {
    private static NetworkTransaction instance = null;
    private NetworkTransaction() {super(); }
    public synchronized static NetworkTransaction inst(){
        if (instance == null) {
            instance = new NetworkTransaction();
            Log.d("NetworkTransaction", "Create new instance");
        }
        return instance;
    }
}
