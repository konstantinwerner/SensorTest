package in.konstant.BT;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BTControl {
    public static final int REQ_ENABLE_BT = 2;

    public static boolean available() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter != null) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean enabled() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            return true;
        } else {
            return false;
        }
    }

    public static void enable(Context context) {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        ((Activity) context).startActivityForResult(enableIntent, REQ_ENABLE_BT);
    }

    public static void disable() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter != null) {
            mBluetoothAdapter.disable();
        }
    }

    public static boolean getBluetoothEnabled(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                return true;
            } else {
                return false;
            }
        } else {
                return true;
        }
    }

    public static void registerStateChangeReceiver(Context context, BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(receiver, filter);
    }
}


