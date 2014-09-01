package in.konstant.Sensors;

import android.util.Log;

import java.util.ArrayList;

public class Sensor {
    public final static String TAG = "Sensor";
    public final static boolean DBG = true;

    private final int id;
    private final String name;
    private final String part;
    private final int numberOfMeasurements;

    private ArrayList<Measurement> measurements;

    public Sensor(int id, String name, String part, int numberOfMeasurements) {
        this.id = id;
        this.name = name;
        this.part = part;
        this.numberOfMeasurements = numberOfMeasurements;
    }

    public Sensor(String[] args) {
        this.id   = Integer.parseInt(args[1]);
        this.name = args[2];
        this.part = args[3];
        this.numberOfMeasurements = Integer.parseInt(args[4]);

        if (DBG) Log.d(TAG, "Sensor[" + id + "] = " + name + " (" + part + ")");
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getPart() {
        return this.part;
    }

    public int getNumberOfMeasurements() {
        return measurements.size();
    }

    public Measurement getMeasurement(int id) {
        return measurements.get(id);
    }

    public void addMeasurement(int id, Measurement measurement) {
        if (measurements.size() > id) {
            measurements.set(id, measurement);      // Replace existing Measurement
        } else {
            measurements.add(measurement);          // Add new Measurement
        }
    }
}





