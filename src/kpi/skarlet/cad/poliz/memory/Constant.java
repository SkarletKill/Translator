package kpi.skarlet.cad.poliz.memory;

import kpi.skarlet.cad.lexer.VariableType;

public class Constant {
    private VariableType type;
    private Float value;

    public Constant(float value, VariableType type) {
        this.value = value;
        this.type = type;
    }

    public VariableType getType() {
        return type;
    }

    public void setType(VariableType type) {
        this.type = type;
    }

    public Float getValue() {
        return (type.equals(VariableType.FLOAT)) ? value : (float) value.intValue();
    }

    public void setValue(float value) {
        this.value = value;
    }
}
