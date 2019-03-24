package kpi.skarlet.cad.lexer;

public enum VariableType {
    INT("int"),
    FLOAT("float");

    private final String name;

    VariableType(String type){
        this.name = type;
    }

    @Override
    public String toString() {
        return VariableType.this.name;
    }
}
