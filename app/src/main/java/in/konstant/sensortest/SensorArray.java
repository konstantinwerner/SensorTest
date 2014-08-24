package in.konstant.sensortest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import in.konstant.BT.BTControl;
import in.konstant.BT.BTDeviceList;
import in.konstant.R;
import in.konstant.Sensors.SensorDevice;

public class SensorArray extends Activity implements SensorDeviceListDialog.SensorDeviceListDialogListener {
    private static final String TAG = "SensorArray";
    private static final boolean DBG = true;

    public static final String PREFS_NAME = "SensorDeviceList";
    private static final String DEVICE_ADDRESSES = "deviceAddresses";

    private SensorArrayAdapter mSensorDevices;

    private boolean enableToasts = false;

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

        mSensorDevices = new SensorArrayAdapter(this);

        loadDeviceList();

        prepareListView();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (DBG) Log.d(TAG, "onStart()");

        enableToasts = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DBG) Log.d(TAG, "onResume()");

    }

    @Override
    public void onPause() {
        super.onPause();
        if (DBG) Log.d(TAG, "onPause()");
    }

    @Override
    public void onStop() {
        if (DBG) Log.d(TAG, "onStop()");

        saveDeviceList();

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (DBG) Log.d(TAG, "onDestroy()");

        enableToasts = false;

        for (int d = 0; d < mSensorDevices.getCount(); d++) {
            mSensorDevices.getItem(d).quit();
        }

        BTControl.unregisterStateChangeReceiver(this, BTStateChangeReceiver);
        super.onDestroy();
    }

    private void saveDeviceList() {
        SharedPreferences deviceList = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = deviceList.edit();

        editor.putStringSet(DEVICE_ADDRESSES, mSensorDevices.getKeySet());

        editor.commit();
    }

    private void loadDeviceList() {
        SharedPreferences deviceList = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        Set<String> addresses = deviceList.getStringSet(DEVICE_ADDRESSES, new HashSet<String>());

        for (String address : addresses) {
            addDevice(address);
        }
    }

    private void addDevice(String address) {
        SensorDevice newDevice = new SensorDevice(this, address);
        newDevice.setCallback(mDeviceHandler);

        mSensorDevices.add(address, newDevice);
    }

    private final BroadcastReceiver BTStateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DBG) Log.d(TAG, "BTStateChangeReceiver()");

            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                            mSensorDevices.notifyDataSetChanged();
                        break;

                    case BluetoothAdapter.STATE_OFF:
                            for (int d = 0; d < mSensorDevices.getCount(); d++) {
                                mSensorDevices.getItem(d).disconnect();
                            }

                            mSensorDevices.notifyDataSetChanged();
                        break;
                }
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (DBG) Log.d(TAG, "onActivityResult(" + requestCode + ", " + resultCode + ")");

        String address = BTDeviceList.getDeviceAddress(requestCode, resultCode, data);

        if (address != null) {
            if (mSensorDevices.contains(address)) {
                toast(getResources().getString(R.string.toast_device_already_on_list));
            } else {
                addDevice(address);
            }
        }
    }

    private final Handler mDeviceHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (DBG) Log.d(TAG, "DeviceHandler(" + msg.what + ", " + (String) msg.obj + ")");

            String address = (String) msg.obj;
            String name = mSensorDevices.getItem(address).getDeviceName();

            switch (msg.what) {
                case SensorDevice.MESSAGE.CREATED:
                    toast(String.format(
                            getResources().getString(R.string.toast_device_added),
                            name));
                    break;

                case SensorDevice.MESSAGE.CONNECTING:
                    mSensorDevices.notifyDataSetChanged();
                    toast(String.format(
                            getResources().getString(R.string.toast_connecting),
                            name));
                    break;

                case SensorDevice.MESSAGE.DESTROYED:
                    mSensorDevices.remove(address);
                    toast(String.format(
                            getResources().getString(R.string.toast_device_deleted),
                            name));
                    break;

                case SensorDevice.MESSAGE.CONNECTED:
                    mSensorDevices.notifyDataSetChanged();
                    toast(String.format(
                            getResources().getString(R.string.toast_connected),
                            name));
                    break;

                case SensorDevice.MESSAGE.CONNECTION_FAILED:
                    mSensorDevices.notifyDataSetChanged();
                    toast(String.format(
                            getResources().getString(R.string.toast_connection_failed),
                            name));
                    break;

                case SensorDevice.MESSAGE.CONNECTION_LOST:
                    mSensorDevices.notifyDataSetChanged();
                    toast(String.format(
                            getResources().getString(R.string.toast_connection_lost),
                            name));
                    break;

                case SensorDevice.MESSAGE.DISCONNECTED:
                    mSensorDevices.notifyDataSetChanged();
                    toast(String.format(
                            getResources().getString(R.string.toast_disconnected),
                            name));
                    break;

                default:
                    break;
            }
        }
    };

    private void toast(String text) {
        if (enableToasts)
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void prepareListView() {
        ListView SensorDeviceList = (ListView) findViewById(R.id.lvSensorDevices);
        SensorDeviceList.setAdapter(mSensorDevices);

        SensorDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showSensorDeviceListDialog(position);
            }
        });

        SensorDeviceList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                return false;
            }
        });
    }

    public void showSensorDeviceListDialog(int id) {
        DialogFragment dialog = new SensorDeviceListDialog();

        Bundle args = new Bundle();

        args.putInt(SensorDeviceListDialog.ARG_ID, id);
        args.putString(SensorDeviceListDialog.ARG_NAME, mSensorDevices.getItem(id).getDeviceName());
        args.putBoolean(SensorDeviceListDialog.ARG_CONNECTED, mSensorDevices.getItem(id).getConnected());

        dialog.setArguments(args);
        dialog.show(getFragmentManager(), "showSensorDeviceListDialog");
    }

    @Override
    public void onSensorDeviceListDialogConnect(int id, boolean connected) {
        if (DBG) Log.d(TAG, "Connect/Disconnect Device " + id);

        if (connected) {
            mSensorDevices.getItem(id).disconnect();
        } else {
            if (BTControl.enabled()) {
                mSensorDevices.getItem(id).connect();
            } else {
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.dialog_nobt_title)
                        .setMessage(String.format(
                                getResources().getString(R.string.dialog_nobt_message),
                                mSensorDevices.getItem(id).getDeviceName()))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BTControl.enable(SensorArray.this);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        }
    }

    @Override
    public void onSensorDeviceListDialogSettings(int id) {
        mSensorDevices.getItem(id).sendCommand("{a} {b|0} ");
    }

    @Override
    public void onSensorDeviceListDialogDelete(int id) {
        if (DBG) Log.d(TAG, "Delete Device " + id);

        final int deviceId = id; // For Access from inner Class

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(String.format(
                            getResources().getString(R.string.dialog_delete_message),
                            mSensorDevices.getItem(id).getDeviceName()))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSensorDevices.getItem(deviceId).quit();
                    }
                })
                .setNegativeButton("No", null)
                .show();
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
                if (BTControl.enabled()) {
                    BTControl.disable();
                } else {
                    BTControl.enable(this);
                }
                return true;

            case R.id.menu_item_add_device:
                BTDeviceList.show(this);
                return true;

            case R.id.menu_item_quit:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean mBTenabled = BTControl.enabled();

        menu.findItem(R.id.menu_item_enable_bt).setChecked(mBTenabled);
        menu.findItem(R.id.menu_item_add_device).setEnabled(mBTenabled);

        return super.onPrepareOptionsMenu(menu);
    }
}
