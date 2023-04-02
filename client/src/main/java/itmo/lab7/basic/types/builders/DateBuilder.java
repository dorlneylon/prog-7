package itmo.lab7.basic.types.builders;

import itmo.lab7.basic.types.builders.annotations.NotNull;

import java.util.Date;

public class DateBuilder implements Builder {
    @NotNull
    private Date date;

    @Override
    public Date build() {
        return date;
    }
}
