package in.konstant.Sensors;

public class Sensor {
    private final String name;
    private final String part;

    private final int numberOfMeasurements;
    private Measurement[] measurements;

    public Sensor(String name, String part, int numberOfMeasurements) {
        this.name = name;
        this.part = part;
        this.numberOfMeasurements = numberOfMeasurements;
    }

    public String getName() {
        return this.name;
    }

    public String getPart() {
        return this.part;
    }

    public int getNumberOfMeasurements() {
        return this.numberOfMeasurements;
    }

    public Measurement getMeasurement(int index) {
        if (index > -1 && index < this.numberOfMeasurements && index < measurements.length) {
            return measurements[index];
        } else {
            throw new IllegalArgumentException();
        }
    }
}





