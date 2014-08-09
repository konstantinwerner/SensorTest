package in.konstant.Sensors;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import in.konstant.BT.BTDevice;

public class CommandHandler {
    private final static String TAG = "CommandHandler";
    private final static boolean DBG = true;

    private static final char CmdStartChar = '{';
    private static final char CmdStopChar = '}';
    private static final char CmdDelimiter = '|';

    private BTThread mBTThread;
    private BuildThread mBuildThread;

    CommandHandler() {
        if (DBG) Log.d(TAG, "CommandHandler()");

        mBTThread = new BTThread();
        mBuildThread = new BuildThread();
    }

    public void destroy() {
        if (DBG) Log.d(TAG, "destroy()");
        mBTThread.cancel();
        mBuildThread.cancel();
    }

    public void connect(String address) {
        if (DBG) Log.d(TAG, "connect(" + address + ")");
        mBTThread.mBTDevice.connect(address);
    }

    public void disconnect() {
        if (DBG) Log.d(TAG, "disconnect()");
        mBTThread.mBTDevice.disconnect();
    }

    public boolean isConnected() {
        return mBTThread.mBTDevice.isConnected();
    }

    public String send(String cmd, boolean waitForReply) {
        if (DBG) Log.d(TAG, "send(" + cmd + ")");

        mBTThread.mBTDevice.send(cmd.getBytes());

        if (waitForReply && mBTThread.mBTDevice.isConnected()) {
            try {
                return mBuildThread.getReply();
            } catch (InterruptedException ie) {
                return null;
            }
        } else {
            return null;
        }
    }

    private class BuildThread extends Thread {
        private static final String TAG = "BuildThread";

        private boolean mRunning = false;
        private boolean mBuilding = false;

        private String partCmd;
        private String buildCmd;

        public BuildThread() {
            if (DBG) Log.d(TAG, "BuildThread()");
            mRunning = true;

            partCmd = new String();
        }

        public void cancel() {
            if (DBG) Log.d(TAG, "cancel()");
            mRunning = false;
        }

        public void run() {
            if (DBG) Log.d(TAG, "BuildThread BEGIN");

            while (mRunning) {

            }

            if (DBG) Log.d(TAG, "BuildThread END");
        }

        public synchronized void putPart(String part) {
            if (DBG) Log.d(TAG, "putPart(" + part + ")");
            int start = part.indexOf(CmdStartChar);
            int stop  = part.indexOf(CmdStopChar);

            mBuilding = true;

            if (start != -1) {
                if (stop != -1) {
                    if (start < stop) {
                        // Complete Command
                        buildCmd = part.substring(start, stop);
                        mBuilding = false;
                        notifyAll();
                    } else {
                        // End of last Command and Start of new Command (should not occur)
                        buildCmd = partCmd + part.substring(0, stop);
                        partCmd = part.substring(start);
                        mBuilding = false;
                        notifyAll();
                    }
                } else {
                    // Start of new Command
                    partCmd = part.substring(start);
                }
            } else {
                if (stop != -1) {
                    // End of last Command
                    buildCmd = partCmd + part.substring(0, stop);
                    partCmd = new String();
                    mBuilding = false;
                    notifyAll();
                } else {
                    // Middle of Command
                    partCmd += part;
                }
            }
        }

        public synchronized String getReply() throws InterruptedException {
            if (DBG) Log.d(TAG, "getReply()");

            if (mBTThread.mBTDevice.isConnected()) {
                while (mBuilding) {
                    wait();
                }

                String tmp = buildCmd;
                buildCmd = null;

                return tmp;
            } else {
                return null;
            }
        }
    }

    private class BTThread extends Thread {
        private static final String TAG = "BTThread";

        public BTDevice mBTDevice;

        private boolean mRunning = false;

        public BTThread() {
            if (DBG) Log.d(TAG, "BTThread()");
            mBTDevice = new BTDevice(BTHandler);

            mRunning = true;
        }

        public void cancel() {
            if (DBG) Log.d(TAG, "cancel()");
            mRunning = false;
            mBTDevice.destroy();
        }

        public void run() {
            if (DBG) Log.d(TAG, "BTThread BEGIN");

            while (mRunning) {

            }

            if (DBG) Log.d(TAG, "BTThread END");
        }

        private final Handler BTHandler = new Handler() {
            private static final String TAG = "BTHandler";

            @Override
            public void handleMessage(Message msg) {
                if (DBG) Log.d(TAG, "handleMessage(" + msg +")");

                switch (msg.what) {
                    case BTDevice.Notification.CONNECTED:
                        break;

                    case BTDevice.Notification.CONNECTION_FAILED:
                    case BTDevice.Notification.CONNECTION_LOST:
                    case BTDevice.Notification.DISCONNECTED:
                        break;

                    case BTDevice.Notification.DATA_RECEIVED:
                        String data = msg.getData().getString(BTDevice.EXTRA_DATA);
                        mBuildThread.putPart(data);
                        break;

                    case BTDevice.Notification.DATA_SENT:
                        break;
                }
            }
        };
    };
}
