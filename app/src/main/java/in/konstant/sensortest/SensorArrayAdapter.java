package in.konstant.sensortest;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import in.konstant.R;
import in.konstant.Sensors.InternalSensorDevice;
import in.konstant.Sensors.Sensor;
import in.konstant.Sensors.SensorDevice;

public class SensorArrayAdapter extends BaseExpandableListAdapter {
    private final static String TAG = "SensorArrayAdapter";
    private final static boolean DBG = true;

    private final Activity context;

    private ArrayList<String> ids;
    private HashMap<String, SensorDevice> devices;

    static class DeviceViewHolder {
        public TextView name;
        public TextView address;
        public View connected;
        public TextView nrOfSensors;
    }

    static class SensorViewHolder {
        public TextView name;
        public TextView part;
        public TextView nrOfMeasurements;
    }

    public SensorArrayAdapter(Activity context) {
        this.context = context;

        devices = new HashMap<String, SensorDevice>();
        ids = new ArrayList<String>();
    }

    @Override
    public SensorDevice getGroup(int id) {
            return devices.get(ids.get(id));
    }

    public SensorDevice getGroup(String address) {
        return devices.get(address);
    }

    public Sensor getChild(int id, int childId) {
        return getGroup(id).getSensor(childId);
    }

    @Override
    public long getGroupId(int id) {
        return id;
    }

    @Override
    public long getChildId(int id, int childId) {
        return childId;
    }

    @Override
    public int getGroupCount() {
        return ids.size();
    }

    @Override
    public int getChildrenCount(int id) {
       return getGroup(id).getNumberOfSensors();
    }

    @Override
    public boolean isChildSelectable(int groupId, int childId) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public Set<String> getKeySet() {
        return devices.keySet();
    }

    public void clear() {
        devices.clear();
        ids.clear();
        notifyDataSetInvalidated();
    }

    public void add(String address, SensorDevice device) {
        devices.put(address, device);
        ids.add(address);
        notifyDataSetChanged();
    }

    public void remove(String address) {
        if (DBG) Log.d(TAG, "remove(" + address + ")");
        this.remove(ids.indexOf(address));
    }

    public void remove(int id) {
        if (DBG) Log.d(TAG, "remove(" + id + ")");
        devices.remove(ids.get(id));
        ids.remove(id);
        notifyDataSetChanged();
    }

    public boolean contains(String address) {
        return devices.containsKey(address);
    }

    @Override
    public View getGroupView(int id, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.arrayadapter_sensorarray_group, null);

            DeviceViewHolder deviceViewHolder = new DeviceViewHolder();
            deviceViewHolder.name = (TextView) convertView.findViewById(R.id.tvDeviceName);
            deviceViewHolder.address = (TextView) convertView.findViewById(R.id.tvDeviceAddress);
            deviceViewHolder.connected = (View) convertView.findViewById(R.id.inDeviceConnected);
            deviceViewHolder.nrOfSensors = (TextView) convertView.findViewById(R.id.tvDeviceNrOfSensors);

            convertView.setTag(deviceViewHolder);
        }

        DeviceViewHolder holder = (DeviceViewHolder) convertView.getTag();
        SensorDevice item = getGroup(id);

        holder.name.setText(item.getDeviceName());
        holder.address.setText(item.getBluetoothAddress());

        if (item.getConnected()) {
            int nrOfSensors = item.getNumberOfSensors();

            holder.nrOfSensors.setText(parent.getContext().getResources().getQuantityString(
                            R.plurals.device_list_nrOfSensors,
                            nrOfSensors,
                            nrOfSensors)
            );
        } else {
            holder.nrOfSensors.setText("");
        }

        int ColorId;

        switch (item.getConnectionState()) {
            case SensorDevice.STATE.CONNECTED: ColorId = R.color.connected; break;
            case SensorDevice.STATE.CONNECTING: ColorId = R.color.connecting; break;
            default:
            case SensorDevice.STATE.DISCONNECTED: ColorId = R.color.disconnected; break;
        }

        holder.connected.setBackgroundColor(parent.getContext().getResources().getColor(ColorId));

        return convertView;
    }

    @Override
    public View getChildView(int id,
                             final int childId,
                             boolean isLastChild,
                             View convertView,
                             ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.arrayadapter_sensorarray_child, null);

            SensorViewHolder sensorViewHolder = new SensorViewHolder();
            sensorViewHolder.name = (TextView) convertView.findViewById(R.id.tvSensorName);
            sensorViewHolder.part = (TextView) convertView.findViewById(R.id.tvSensorPart);
            sensorViewHolder.nrOfMeasurements = (TextView) convertView.findViewById(R.id.tvSensorNoOfMeasurements);

            convertView.setTag(sensorViewHolder);
        }

        SensorViewHolder holder = (SensorViewHolder) convertView.getTag();
        Sensor item = getGroup(id).getSensor(childId);

        holder.name.setText(item.getName());
        holder.part.setText(item.getPart());

        int nrOfMeasurements = item.getNumberOfMeasurements();

        holder.nrOfMeasurements.setText(parent.getContext().getResources().getQuantityString(
                        R.plurals.device_list_nrOfMeasurements,
                        nrOfMeasurements,
                        nrOfMeasurements)
        );

        return convertView;
    }
}
