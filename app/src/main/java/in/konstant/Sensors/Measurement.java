package in.konstant.Sensors;

public class Measurement {
    private final int sensorId;
    private final int id;
    private final String name;       // Name of the Measurement (e.g. "Acceleration")
    private final Range ranges[];    // Ranges of the Measurement (e.g. -8g to +8g)
    private final Type type;         // Type of the Measurement (Float, Int, ...)
    private final Unit unit;         // Unit of the Measurement (e.g. m/s²)
    private final int size;          // Number of Values (e.g. 3 (x,y,z))
    private final int duration;      // Duration of one Measurement in Milliseconds

    private int range;               // Currently active range

    public Measurement(int sensorId, int id, String name, Range[] ranges, int range, Type type, Unit unit, int size, int duration) {
        this.sensorId = sensorId;
        this.id = id;
        this.name = name;
        this.ranges = ranges;
        this.range = range;
        this.type = type;
        this.unit = unit;
        this.size = size;
        this.duration = duration;
    }

    public Measurement(String[] args) {
        sensorId = Integer.parseInt(args[1]);
        id = Integer.parseInt(args[2]);
        name = args[3];
        duration = Integer.parseInt(args[4]);
        type = Type.values()[Integer.parseInt(args[5])];
        size = Integer.parseInt(args[6]);

        int noOfRanges = Integer.parseInt(args[7]);

        ranges = new Range[noOfRanges];

        for (int r = 0; r < noOfRanges; r++) {
            float min = 0;
            float max = 0;
            int digits = 0;

            if (type == Type.TYPE_FLOAT) {
                min = ASCII85.decodeToFloat(args[8 + r * 3]);
                max = ASCII85.decodeToFloat(args[9 + r * 3]);
            } else {
                min = ASCII85.decodeToInt(args[8 + r * 3]);
                max = ASCII85.decodeToInt(args[9 + r * 3]);
            }
            digits = Integer.parseInt(args[10 + r * 3]);

            ranges[r] = new Range(min, max, digits);
        }
    }

    public int getSensorId() {
        return this.sensorId;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Range[] getRanges() {
        return this.ranges;
    }

    public Range getRange(int id) {
        return this.ranges[id];
    }

    public Range getCurrentRange() {
        return this.ranges[range];
    }

    public int getCurrentRangeId() {
        return this.range;
    }

    public Type getType() {
        return this.type;
    }

    public Unit getUnit() {
        return this.unit;
    }

    public int getSize() {
        return this.size;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setRange(int range) {
        this.range = range;
    }
}
