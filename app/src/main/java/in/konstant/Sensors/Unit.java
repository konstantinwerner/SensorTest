package in.konstant.Sensors;

public class Unit {
    public final String name;
    public final String symbol;
    public final Prefix prefix;
    public final Subunit baseunits[];

    public Unit(String name, String symbol, Prefix prefix, Subunit baseunits[]) {
        this.name = name;
        this.symbol = symbol;
        this.prefix = prefix;
        this.baseunits = baseunits;
    }

    @Override
    public String toString() {
        StringBuilder num = new StringBuilder();
        StringBuilder den = new StringBuilder();

        for (int i = 0; i < this.baseunits.length; i++) {

            if (this.baseunits[i].exponent < 0) {
                den.append(this.baseunits[i].toString());
                den.append(" ");
            } else if (this.baseunits[i].exponent > 0)
            {
                num.append(this.baseunits[i].toString());
                num.append(" ");
            }
        }

        return (num.toString() + " per " + den.toString());
    }
}