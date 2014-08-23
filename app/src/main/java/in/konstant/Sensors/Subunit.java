package in.konstant.Sensors;

public class Subunit {
    public final Dimension dimension;
    public final int exponent;

    public Subunit(Dimension dimension, int exponent) {
        this.dimension = dimension;
        this.exponent = exponent;
    }

    public String toSymbol() {
        switch (this.exponent) {
            case 1:
                return this.dimension.toSymbol();
            case 2:
                return (this.dimension.toSymbol() + "²");
            case 3:
                return (this.dimension.toSymbol() + "³");
            default:
                return (dimension.toSymbol() + "^" + exponent);
        }
    }

    @Override
    public String toString() {
        switch (this.exponent) {
            case 1:
                return this.dimension.toString();
            case 2:
                return ("square" + this.dimension.toString());
            case 3:
                return ("cubic" + this.dimension.toString());
            default:
                return (this.dimension.toString() + "^" + this.exponent);
        }
    }
}