package in.konstant.Sensors;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.util.Arrays;

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
    }

    public static final class STATE  {
        public static final int DISCONNECTED = BTDevice.STATE.DISCONNECTED;
        public static final int CONNECTING = BTDevice.STATE.CONNECTING;
        public static final int CONNECTED = BTDevice.STATE.CONNECTED;
    }

    private BTDevice mBTDevice;
    private Handler BTHandler, mCallback;

    private final Context mContext;

    private String mAddress;
    private String mName;

// Lifecycle Management-----------------------------------------------------------------------------

    public SensorDevice(Context context, String address) {
        super(address);
        if (DBG) Log.d(TAG, "SensorDevice(" + address + ") created");

        mContext = context;
        mAddress = address;

        mBTDevice = new BTDevice(mContext, mAddress);
        mName = mBTDevice.getName();

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

    public boolean getConnected() {
        return mBTDevice.isConnected();
    }

    public int getConnectionState() { return mBTDevice.getState(); }

// Commands ----------------------------------------------------------------------------------------

    public void sendCommand(String command) {
        mBTDevice.send(command.getBytes());
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
    private final static byte START_CHAR = '{';
    private final static byte STOP_CHAR = '}';

    private void handleDataReceived(byte[] data) {
        if (DBG) Log.d(TAG, "handleDataReceived(" + data.length + ")");

        for (int b = 0; b < data.length && data[b] != 0; ++b) {
            switch (data[b]) {
                case START_CHAR:
                    queue = "";
                    break;

                case STOP_CHAR:
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

    private void processReply(String reply) {
        if (DBG) Log.d(TAG, "processReply(" + reply + ")");

        String[] args = reply.split("[|]+");

        for (int a = 0; a < args.length; a++) {
            if (DBG) Log.d(TAG, "Arg[" + a +"] = " + args[a]);
        }


    }
}
