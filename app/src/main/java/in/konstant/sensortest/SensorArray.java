package in.konstant.sensortest;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.HashMap;

import in.konstant.BT.BTControl;
import in.konstant.BT.BTDevice;
import in.konstant.BT.BTDeviceList;
import in.konstant.R;
import in.konstant.Sensors.SensorDevice;

public class SensorArray extends Activity {
    private static final String TAG = "SensorList";
    private static final boolean DBG = true;

    private boolean mBTenabled = false;

    private HashMap<String, SensorDevice> mSensorDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DBG) Log.d(TAG, "onCreate()");

        setContentView(R.layout.activity_sensor_list);

        if (!BTControl.available()) {
            Toast.makeText(this, R.string.toast_not_available, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        BTControl.registerStateChangeReceiver(this, BTStateChangeReceiver);

        mSensorDevices = new HashMap<String, SensorDevice>();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (DBG) Log.d(TAG, "onStart()");

        mBTenabled = BTControl.enabled();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DBG) Log.d(TAG, "onResume()");

        mBTenabled = BTControl.enabled();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (DBG) Log.d(TAG, "onPause()");
    }

    @Override
    public void onStop() {
        if (DBG) Log.d(TAG, "onStop()");

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (DBG) Log.d(TAG, "onDestroy()");

        for (ArrayMap.Entry<String, SensorDevice> entry : mSensorDevices.entrySet()) {
            entry.getValue().quit();
        }

        super.onDestroy();
    }

    private final BroadcastReceiver BTStateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DBG) Log.d(TAG, "BTStateChangeReceiver()");

            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        mBTenabled = true;
                        break;

                    case BluetoothAdapter.STATE_OFF:
                        mBTenabled = false;
                        break;
                }
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (DBG) Log.d(TAG, "onActivityResult(" + requestCode + ", " + resultCode + ")");

        String address = BTDeviceList.getDeviceAddress(requestCode, resultCode, data);

        if (address != null) {
            SensorDevice newDevice = new SensorDevice(this);
            newDevice.setCallback(mDeviceHandler);
            mSensorDevices.put(address, newDevice);
        } else {

        }
    }

    private final Handler mDeviceHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (DBG) Log.d(TAG, "DeviceHandler(" + msg.what + ")");

            switch (msg.what) {
                case SensorDevice.MESSAGE.CREATED:
                    mSensorDevices.get((String) msg.obj).connect((String) msg.obj);

                    toast(String.format(
                            getResources().getString(R.string.toast_device_added),
                            (String) msg.obj));
                    break;

                case SensorDevice.MESSAGE.DESTROYED:
                    mSensorDevices.remove((String) msg.obj);
                    toast(String.format(
                            getResources().getString(R.string.toast_device_removed),
                            (String) msg.obj));
                    break;

                case SensorDevice.MESSAGE.CONNECTED:
                    toast(String.format(
                            getResources().getString(R.string.toast_connected),
                            (String) msg.obj));
                    break;

                case SensorDevice.MESSAGE.CONNECTION_FAILED:
                    toast(String.format(
                            getResources().getString(R.string.toast_connection_failed),
                            (String) msg.obj));
                    break;

                case SensorDevice.MESSAGE.CONNECTION_LOST:
                    toast(String.format(
                            getResources().getString(R.string.toast_connection_lost),
                            (String) msg.obj));
                    break;

                case SensorDevice.MESSAGE.DISCONNECTED:
                    toast(String.format(
                            getResources().getString(R.string.toast_disconnected),
                            (String) msg.obj));
                    break;

                default:
                    break;
            }
        }
    };

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sensor_array, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_enable_bt:
                if (mBTenabled) {
                    BTControl.disable();
                    mBTenabled = false;
                } else {
                    BTControl.enable(this);
                }
                return true;

            case R.id.menu_item_add_device:
                BTDeviceList.show(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_item_enable_bt).setChecked(mBTenabled);
        menu.findItem(R.id.menu_item_add_device).setEnabled(mBTenabled);

        return super.onPrepareOptionsMenu(menu);
    }
}
