package kpi.skarlet.cad.lexer.constants;

public interface ErrorConstants {

    String WRONG_TYPE = "Invalid data type";
    String WRONG_OPERATOR_LIST = "Wrong list of operators";
    String WRONG_AD_LIST = "Wrong list of ads";
    String WRONG_AD = "Wrong ad";
    String WRONG_ID_LIST = "Wrong list of identifiers";
    String WRONG_STATEMENT_BLOCK = "Wrong statement block";
    String WRONG_INPUT = "Invalid input";
    String WRONG_OUTPUT = "Invalid output";
    String WRONG_LOOP = "Invalid loop";
    String WRONG_CONDITIONAL = "Invalid conditional";
    String WRONG_ASSIGNMENT = "Invalid assignment";
    String WRONG_LABEL = "Wrong label call syntax";
    String WRONG_LE = "Wrong logical expression";
    String WRONG_LT = "Wrong logical expression";
    String WRONG_LF = "Wrong logical expression";
    String WRONG_E = "Wrong expression";
    String WRONG_E_FIRST = "Wrong first expression";
    String WRONG_E_SECOND = "Wrong second expression";
    String WRONG_LT_FIRST = "Wrong first 'or' clause";
    String WRONG_LT_SECOND = "Wrong second 'or' clause";
    String WRONG_LF_FIRST = "Wrong first 'and' clause";
    String WRONG_LF_SECOND = "Wrong second 'and' clause";
    String WRONG_T_FIRST = "Wrong first T clause";
    String WRONG_T_SECOND = "Wrong second T clause";
    String WRONG_V = "Expected variable or expression";
    String WRONG_V_FIRST = "Wrong first V clause";
    String WRONG_V_SECOND = "Wrong second V clause";
    String WRONG_NEGATION_BEFORE_EXPRESSION = "Invalid 'not' clause";
    String WRONG_SIGN = "Invalid sign";

    String MISSING_OPENING_BRACE = "Missing {";
    String MISSING_CLOSING_BRACE = "Missing }";
    String MISSING_OPENING_BRACKET = "Missing (";
    String MISSING_CLOSING_BRACKET = "Missing )";
    String MISSING_SEMICOLON = "Missing ;";
    String MISSING_EQUAL = "Missing =";
    String MISSING_OPERATOR = "Missing operator or end of list";
    String MISSING_SEMICOLON_AFTER_OP = "Missing semicolon after first operator";
    String MISSING_OPERATOR_FIRST = "Missing first operator";
    String MISSING_AD = "Missing ad";
    String MISSING_IDENTIFIER = "Missing identifier";
    String MISSING_AD_FIRST = "Missing first ad";
    String MISSING_IDLIST_COMMA = "Missing comma in between identifiers";
    String MISSING_IDLIST_FIRST_ID = "Missing first identifier";

    String EXPECTED_TYPE = "Type expected";
    String EXPECTED_SEMICOLON = "semicolon expected";
    String EXPECTED_EXPRESSION = "Expression expected";
    String EXPECTED_LABEL = "Label expected";
    String EXPECTED_INPUT_VARIABLE = "Identifier or constant expected";
    String EXPECTED_INPUT_OPERATOR = scopes(TerminalSymbols.INPUT_OPERATOR) + "expected";
    String EXPECTED_INPUT_JOINT = scopes(TerminalSymbols.INPUT_JOINT) + "expected";
    String EXPECTED_OUTPUT_VARIABLE = "Identifier or constant expected";
    String EXPECTED_OUTPUT_OPERATOR = scopes(TerminalSymbols.OUTPUT_OPERATOR) + "expected";
    String EXPECTED_OUTPUT_JOINT = scopes(TerminalSymbols.OUTPUT_JOINT) + "expected";
    String EXPECTED_LOOP_ASSIGNMENT = "Assignment in the cycle description expected";
    String EXPECTED_LOOP_LE = "Logical expression in the cycle description expected";


    static String scopes(String msg) {
        return "'" + msg + "'";
    }
}
