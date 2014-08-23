package in.konstant.Sensors;

public enum Dimension {
    NONE,
    METER,
    KILOGRAM,
    SECOND,
    AMPERE,
    KELVIN,
    MOLE,
    CANDELA,
    DEGREE;

    @Override
    public String toString() {
        switch (this) {
            case NONE:
                return "";
            default:
                return this.name();
        }
    }

    public String toSymbol() {
        switch (this) {
            default:
            case NONE:
                return "";
            case METER:
                return "m";
            case KILOGRAM:
                return "kg";
            case SECOND:
                return "s";
            case AMPERE:
                return "A";
            case KELVIN:
                return "K";
            case CANDELA:
                return "cd";
            case DEGREE:
                return "°";
        }
    }
}