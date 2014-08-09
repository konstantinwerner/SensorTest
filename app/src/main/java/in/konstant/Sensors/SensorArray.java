package in.konstant.Sensors;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SensorArray {
    private final static String TAG = "SensorArray";
    private final static boolean DBG = true;

    private CommandHandler mCommandHandler;

    private int mNumberOfSensors = -1;


    public SensorArray() {
        mCommandHandler = new CommandHandler();

    }

    public void destroy() {
        mCommandHandler.destroy();
    }

    public void connect(String address) {
        mCommandHandler.connect(address);
    }

    public boolean init() {
           if (mCommandHandler.isConnected()) {
               String reply;

               reply = mCommandHandler.send("{a} ", true);
               if (DBG) Log.d(TAG, "Init() : " + reply);

               reply = mCommandHandler.send("{b|0} ", true);
               if (DBG) Log.d(TAG, "Init() : " + reply);

               return true;
           } else {
               return false;
           }
    }
}
