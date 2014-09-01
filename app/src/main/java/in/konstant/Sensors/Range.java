package in.konstant.Sensors;

public class Range {
    private final float min;
    private final float max;
    private final int digits;

    public Range(float min, float max, int digits) {
        this.min = min;
        this.max = max;
        this.digits = digits;
    }

    public float getMin() {
        return this.min;
    }

    public float getMax() {
        return this.max;
    }

    public int getDigits() {
        return this.digits;
    }

    // TODO: toString()
}
