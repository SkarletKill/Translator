package kpi.skarlet.cad.forwarding.constants;

public enum GrammarConstants {
    PROGRAM("program"),
    DECLARATION_LIST("declaration list"),
    DECLARATION("declaration"),
    OPERATOR_LIST("operator list"),
    OPERATOR("operator"),
    TYPE("type"),
    VARIABLE_LIST("variable list"),
    IDENTIFIER("identifier"),
    CONSTANT("constant"),
    LABEL("label"),
    LABEL_CALL("label call"),
    INPUT("input"),
    OUTPUT("output"),
    LOOP("loop"),
    CONDITIONAL("conditional"),
    ASSIGNMENT("assignment"),
    LE("LE"),
    LF("LF"),
    LT("LT"),
    R("R"),
    LS("LS"),
    E("E"),
    F("F"),
    T("T"),
    V("V"),
    SIGN("sign"),
//    V("V"),
//    CONDITIONAL("identifier"),
//    DECLARATION_LIST("declaration list"),
    ;
    private String name;

    GrammarConstants(String name) {
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
