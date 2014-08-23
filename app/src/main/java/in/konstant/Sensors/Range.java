package in.konstant.Sensors;

public class Range {
    public final Value min;
    public final Value max;
    public final int digits;

    public Range(Value min, Value max, int digits) {
        this.min = min;
        this.max = max;
        this.digits = digits;
    }

    // TODO: toString()
}
