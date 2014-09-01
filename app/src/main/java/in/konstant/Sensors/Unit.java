package in.konstant.Sensors;

public class Unit {
    public final String name;
    public final String symbol;
    public final Prefix prefix;
    public final Subunit baseUnits[];

    public Unit(String name, String symbol, Prefix prefix, Subunit baseUnits[]) {
        this.name = name;
        this.symbol = symbol;
        this.prefix = prefix;
        this.baseUnits = baseUnits;
    }

    @Override
    public String toString() {
        StringBuilder num = new StringBuilder();
        StringBuilder den = new StringBuilder();

        for (int i = 0; i < this.baseUnits.length; i++) {

            if (this.baseUnits[i].exponent < 0) {
                den.append(this.baseUnits[i].toString());
                den.append(" ");
            } else if (this.baseUnits[i].exponent > 0)
            {
                num.append(this.baseUnits[i].toString());
                num.append(" ");
            }
        }

        return (num.toString() + " per " + den.toString());
    }
}