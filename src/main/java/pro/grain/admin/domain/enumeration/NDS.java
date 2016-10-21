package pro.grain.admin.domain.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The NDS enumeration.
 */
public enum NDS {
    INCLUDED /*("с НДС")*/,
    EXCLUDED /*("без НДС")*/,
    BOTH /*("с НДС + без НДС")*/;
/*
    private final String name;

    NDS(String s) {
        name = s;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    public String toString() {
        return this.name;
    }*/
}
