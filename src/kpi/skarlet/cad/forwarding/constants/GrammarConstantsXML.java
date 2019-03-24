package kpi.skarlet.cad.forwarding.constants;

public enum GrammarConstantsXML {
    RULE("rule"),
    VALUES("values"),
    VAL("v"),
    NAME("name"),
    TYPE("type")
    ;
    private String name;

    GrammarConstantsXML(String name) {
        this.name = name;
    }

    public String str() {
        return name;
    }

    @Override
    public String toString() {
        return this.str();
    }
}
