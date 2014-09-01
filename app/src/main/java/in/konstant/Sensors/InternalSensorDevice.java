package in.konstant.Sensors;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.hardware.*;
import android.hardware.Sensor;

import java.util.List;

import in.konstant.R;

public class InternalSensorDevice {
    private static final String TAG = "InternalSensors";
    private static final boolean DBG = true;

    private final Context context;
    private final SensorManager mSensorManager;
    private List<Sensor> sensors;

    public InternalSensorDevice(Context context) {
        this.context = context;

        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
    }

    public String getDeviceName() {
        return context.getResources().getString(R.string.internal_device_name);
    }

    public String getBluetoothAddress() {
        BluetoothAdapter bluetoothDefaultAdapter = BluetoothAdapter.getDefaultAdapter();

        if ((bluetoothDefaultAdapter != null) && (bluetoothDefaultAdapter.isEnabled())) {
            return BluetoothAdapter.getDefaultAdapter().getAddress();
        } else {
            return "00:00:00:00:00:00";
        }
    }

    public int getNumberOfSensors() {
        return sensors.size();
    }

    public Sensor getSensor(int id) {
        return sensors.get(id);
    }
}
