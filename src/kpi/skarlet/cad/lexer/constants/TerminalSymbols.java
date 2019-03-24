package kpi.skarlet.cad.lexer.constants;

public interface TerminalSymbols {
    String TYPE_INT = "int";
    String TYPE_FLOAT = "float";
    String CYCLE_FOR = "for";
    String LABEL_START = "goto";
    String CONDITIONAL_OPERATOR = "if";
    String INPUT_OPERATOR = "cin";
    String OUTPUT_OPERATOR = "cout";
    String NEGATION = "not";
    String AND = "and";
    String OR = "or";
    String COMMA = ",";
    String EQUAL = "=";
    String INPUT_JOINT = ">>";
    String OUTPUT_JOINT = "<<";
    String COMPARE = "==";
    String COMPARE_NEGATION = "!=";
    String MORE = ">";
    String LESS = "<";
    String MORE_OR_EQUAL = ">=";
    String LESS_OR_EQUAL = "<=";
    String ASTERISK = "*";
    String SLASH = "/";
    String PLUS = "+";
    String MINUS = "-";
    String OPENING_BRACKET = "(";
    String CLOSING_BRACKET = ")";
    String COLON = ":";
    String OPENING_BRACE = "{";
    String CLOSING_BRACE = "}";
    String SEMICOLON = ";";
    String UNDERLINE = "_";
    String EXCLAMINATION = "!";
    String OPENING_SQUARE_BRACE = "[";
    String CLOSING_SQUARE_BRACE = "]";
    String END = "end";

    String IDENTIFIER = "@IDN";
    String CONSTANT = "@CON";
    String LABEL = "@LBL";
}
