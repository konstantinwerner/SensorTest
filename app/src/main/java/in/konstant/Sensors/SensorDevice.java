package in.konstant.Sensors;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;

import in.konstant.BT.BTDevice;

public class SensorDevice extends HandlerThread implements Handler.Callback {
    private final static String TAG = "SensorDevice";
    private final static boolean DBG = true;

    public static final class MESSAGE {
        public static final int CREATED = 1;
        public static final int CONNECTING = 2;
        public static final int CONNECTED = 3;
        public static final int DISCONNECTED = 4;
        public static final int CONNECTION_LOST = 5;
        public static final int CONNECTION_FAILED = 6;
        public static final int DESTROYED = 7;
        public static final int CHANGED = 8;
    }

    public static final class STATE {
        public static final int DISCONNECTED = BTDevice.STATE.DISCONNECTED;
        public static final int CONNECTING = BTDevice.STATE.CONNECTING;
        public static final int CONNECTED = BTDevice.STATE.CONNECTED;
    }

    private static final class CMD {
        public final static char START_CHAR = '{';
        public final static char STOP_CHAR = '}';
        public final static char DELIMITER = '|';

        public static final char GET_NO_SENSORS = 'a';
        public static final char GET_SENSOR_INFO = 'b';
        public static final char GET_SENSOR_MEAS_INFO = 'c';
        public static final char GET_SENSOR_UNIT_INFO = 'd';
        public static final char GET_SENSOR_MEAS = 'e';

        public static final char SET_SENSOR_RANGE = 'f';
        public static final char SET_SENSOR_OFF = 'g';
        public static final char SET_SENSOR_ON = 'h';
    }

    private BTDevice mBTDevice;
    private Handler BTHandler, mCallback;
    private final Context mContext;

    private boolean replyReceived = false;

    private final String mAddress;
    private String mName;

    private int numberOfSensors;
    private ArrayList<Sensor> sensors;

// Lifecycle Management-----------------------------------------------------------------------------

    public SensorDevice(Context context, String address) {
        super(address);
        if (DBG) Log.d(TAG, "SensorDevice(" + address + ") created");

        mContext = context;
        mAddress = address;

        mBTDevice = new BTDevice(mContext, mAddress);
        mName = mBTDevice.getName();

        sensors = new ArrayList<Sensor>();

        start();
    }

    public void setCallback(Handler callback) {
        mCallback = callback;
    }

    @Override
    protected void onLooperPrepared() {
        BTHandler = new Handler(getLooper(), this);

        mBTDevice.setHandler(BTHandler);

        mCallback.sendMessage(Message.obtain(null, MESSAGE.CREATED, mAddress));
    }

    @Override
    public boolean quit() {
        if (DBG) Log.d(TAG, "quit()");
        mCallback.sendMessage(Message.obtain(null, MESSAGE.DESTROYED, mAddress));
        mBTDevice.destroy();
        return super.quit();
    }

// Setter & Getter ---------------------------------------------------------------------------------

    public boolean getConnected() {
        return mBTDevice.isConnected();
    }

    public int getConnectionState() { return mBTDevice.getState(); }

    public String getBluetoothName() {
        return mBTDevice.getName();
    }

    public String getBluetoothAddress() {
        return mAddress;
    }

    public String getDeviceName() {
        return mName;
    }

    public void setDeviceName(String name) {
        mName = name;
    }

    public Sensor getSensor(int id) {
        return sensors.get(id);
    }

    public int getNumberOfSensors() {
        return sensors.size();
    }

// Commands ----------------------------------------------------------------------------------------
    public synchronized void sendCommand(final String command, boolean waitForReply) {
        if (DBG) Log.d(TAG, "Sending CMD: " + command);

        replyReceived = false;

        mBTDevice.send(command.getBytes());

        if (waitForReply) {
            try {
                while (!replyReceived) {
                    wait();
                }
            } catch (InterruptedException ie) { }
        }
    }

    public void queryNumberOfSensors(){
        if (DBG) Log.d(TAG, "Query No. of Sensors");
        sendCommand("{" + CMD.GET_NO_SENSORS + "} ", true);
        if (DBG) Log.d(TAG, "Got No. of Sensors");
    }

    public void querySensorInfo(int sensor) {
        if (DBG) Log.d(TAG, "Query Sensor Info " + sensor);
        if (sensor >= 0 && sensor < 74) { // Printable Ascii Characters between 0 and z
            sendCommand("{" + CMD.GET_SENSOR_INFO + CMD.DELIMITER + (char) ('0' + sensor) + "} ", true);
        }
        if (DBG) Log.d(TAG, "Got Sensor Info " + sensor);
    }

    public void queryMeasurementInfo(int sensor, int measurement) {
        if (DBG) Log.d(TAG, "Query Measurement Info " + sensor + ", " + measurement);
        if (sensor >= 0 && sensor < 74 &&
            measurement >= 0 && measurement < 74) { // Printable Ascii Characters between 0 and z
            sendCommand("{" + CMD.GET_SENSOR_MEAS_INFO + CMD.DELIMITER +
                        (char) ('0' + sensor) + CMD.DELIMITER +
                        (char) ('0' + measurement)+ "} ", true);
        }
        if (DBG) Log.d(TAG, "Got Measurement Info " + sensor + ", " + measurement);
    }

// Connection Management----------------------------------------------------------------------------

    public void connect() {
        if (DBG) Log.d(TAG, "connect()");
        mBTDevice.connect();
    }

    public void disconnect() {
        if (DBG) Log.d(TAG, "disconnect()");
        mBTDevice.disconnect();
    }

    public void init() {
        Runnable initializer = new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

                queryNumberOfSensors();

                for (int s = 0; s < numberOfSensors; s++) {
                    querySensorInfo(s);

                    for (int m = 0; m < sensors.get(s).getNumberOfMeasurements(); m++) {
                        queryMeasurementInfo(s, m);
                    }
                }

                mCallback.sendMessage(Message.obtain(null, MESSAGE.CHANGED, mAddress));
            }
        };

        initializer.run();
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (DBG) Log.d(TAG, "handleMessage(" + msg.what + ")");

        //String address = msg.getData().getString(BTDevice.EXTRA_ADDRESS);

        switch (msg.what) {
            case BTDevice.MESSAGE.CONNECTED:
                handleConnected();
                return true;

            case BTDevice.MESSAGE.CONNECTING:
                handleConnecting();
                return true;

            case BTDevice.MESSAGE.CONNECTION_FAILED:
                handleConnectionFailed();
                return true;

            case BTDevice.MESSAGE.CONNECTION_LOST:
                handleConnectionLost();
                return true;

            case BTDevice.MESSAGE.DISCONNECTED:
                handleDisconnected();
                return true;

            case BTDevice.MESSAGE.DATA_RECEIVED:
                byte[] received = msg.getData().getByteArray(BTDevice.EXTRA_DATA);
                handleDataReceived(received);
                return true;

            case BTDevice.MESSAGE.DATA_SENT:
                byte[] sent = msg.getData().getByteArray(BTDevice.EXTRA_DATA);
                handleDataSent(sent);
                return true;

            default:
                return false;
        }
    }

    private void handleConnected() {
        mCallback.sendMessage(Message.obtain(null, MESSAGE.CONNECTED, mAddress));
    }

    private void handleConnecting() {
        mCallback.sendMessage(Message.obtain(null, MESSAGE.CONNECTING, mAddress));
    }

    private void handleDisconnected() {
        mCallback.sendMessage(Message.obtain(null, MESSAGE.DISCONNECTED, mAddress));
    }

    private void handleConnectionFailed() {
        mCallback.sendMessage(Message.obtain(null, MESSAGE.CONNECTION_FAILED, mAddress));
    }

    private void handleConnectionLost() {
        mCallback.sendMessage(Message.obtain(null, MESSAGE.CONNECTION_LOST, mAddress));
    }

    private String queue = "";

    private void handleDataReceived(byte[] data) {
        if (DBG) Log.d(TAG, "handleDataReceived(" + data.length + ")");

        for (int b = 0; b < data.length && data[b] != 0; ++b) {
            switch (data[b]) {
                case CMD.START_CHAR:
                    queue = "";
                    break;

                case CMD.STOP_CHAR:
                    processReply(queue);
                    break;

                default:
                    queue += new String(new byte[] {data[b]});
                    break;
            }
            data[b] = 0;
        }
    }

    private void handleDataSent(byte[] data) {

    }

    private synchronized void processReply(String reply) {
        if (DBG) Log.d(TAG, "processReply(" + reply + ")");

        String[] args = reply.split("[" + CMD.DELIMITER + "]+");

        if (args.length == 0)
            return;

        switch (args[0].charAt(0)) {
            case CMD.GET_NO_SENSORS:        handleNumberOfSensors(args); break;
            case CMD.GET_SENSOR_INFO:       handleSensorInfo(args); break;
            case CMD.GET_SENSOR_MEAS_INFO:  handleMeasurementInfo(args); break;
            case CMD.GET_SENSOR_UNIT_INFO:  handleUnitInfo(args); break;
            case CMD.GET_SENSOR_MEAS:       handleMeasurement(args); break;
            default:
        }

        replyReceived = true;
        notifyAll();
    }

    private void handleNumberOfSensors(String[] args) {
        this.numberOfSensors = Integer.parseInt(args[1]);

        if (DBG) Log.d(TAG, "Sensors = " + numberOfSensors);

        sensors.ensureCapacity(numberOfSensors);
    }

    private void handleSensorInfo(String[] args) {
        Sensor sensor = new Sensor(args);

        if (sensors.size() > sensor.getId()) {
            sensors.set(sensor.getId(), sensor);  // Replace existing entry
        } else {
            sensors.add(sensor);            // Add new Sensor
        }
    }

    private void handleMeasurementInfo(String[] args) {
        Measurement measurement = new Measurement(args);

        sensors.get(measurement.getSensorId()).addMeasurement(measurement.getId(), measurement);
    }

    private void handleUnitInfo(String[] args) {

    }

    private void handleMeasurement(String[] args) {

    }
}
