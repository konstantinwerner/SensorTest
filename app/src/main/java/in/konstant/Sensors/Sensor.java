package in.konstant.Sensors;

public class Sensor {
    private final int id;
    private final String name;
    private final String part;
    private final int numberOfMeasurements;

    private Measurement[] measurements;

    public Sensor(int id, String name, String part, int numberOfMeasurements) {
        this.id = id;
        this.name = name;
        this.part = part;
        this.numberOfMeasurements = numberOfMeasurements;
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
//        return this.measurements.length; //TODO: For testing
        return numberOfMeasurements;
    }

    public Measurement getMeasurement(int index) {
        if (index > -1 && index < measurements.length) {
            return measurements[index];
        } else {
            throw new IllegalArgumentException();
        }
    }
}





