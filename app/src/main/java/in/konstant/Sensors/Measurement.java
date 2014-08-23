package in.konstant.Sensors;

public class Measurement {
    public final String name;       // Name of the Measurement (e.g. "Acceleration")
    public final Range ranges[];    // Ranges of the Measurement (e.g. -8g to +8g)
    public final Type type;         // Type of the Measurement (Float, Int, ...)
    public final Unit unit;         // Unit of the Measurement (e.g. m/s²)
    public final int size;          // Number of Values (e.g. 3 (x,y,z))
    public final int duration;      // Duration of one Measurement in Milliseconds

    private int range;               // Currently active range

    public Measurement(String name, Range[] ranges, int range, Type type, Unit unit, int size, int duration) {
        this.name = name;
        this.ranges = ranges;
        this.range = range;
        this.type = type;
        this.unit = unit;
        this.size = size;
        this.duration = duration;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getRange() {
        return this.range;
    }
}
