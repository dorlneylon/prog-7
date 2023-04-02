package itmo.lab7.basic.types.builders;

import itmo.lab7.basic.baseclasses.Coordinates;
import itmo.lab7.basic.types.builders.annotations.NotNull;
import itmo.lab7.basic.types.builders.annotations.Value;

public class CoordinatesBuilder implements Builder {

    @Value(min = -521)
    private float x;
    @Value(max = 256)
    @NotNull
    private Integer y;

    @Override
    public Coordinates build() {
        return new Coordinates(x, y);
    }
}
