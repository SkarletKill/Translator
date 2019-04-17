package kpi.skarlet.cad.poliz.memory;

import kpi.skarlet.cad.lexer.VariableType;

public class Variable {
    private String name;
    private VariableType type;
    private Float value;

    public Variable(String name, VariableType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VariableType getType() {
        return type;
    }

    public void setType(VariableType type) {
        this.type = type;
    }

    public Float getValue() {
        if (value == null) return null;
        return (type.equals(VariableType.FLOAT)) ? value : (float) value.intValue();
    }

    public void setValue(float value) {
        this.value = value;
        if(this.type.equals(VariableType.INT)) this.value = (float) this.value.intValue();
    }
}
