package CommonLib;

import android.util.Log;

/**
 * Created by My PC on 26/11/2015.
 */
public class Model {
    private static Model instance = null;
    private Model() { }
    public synchronized static Model inst(){
        if (instance == null) {
            instance = new Model();
            Log.d("Model", "Create new instance");
        }
        return instance;
    }


}
