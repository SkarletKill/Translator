package kpi.skarlet.cad.lexer.constants;

import java.util.HashMap;

public class InitKeywords extends HashMap<String, Integer> {
    private static TerminalSymbols TS;

    {
        put(TS.TYPE_INT, 1);
        put(TS.TYPE_FLOAT, 2);
        put(TS.CYCLE_FOR, 3);
//        put("to", 4);
//        put("step", 5);
        put(TS.LABEL_START, 6);
        put(TS.CONDITIONAL_OPERATOR, 7);
        put(TS.INPUT_OPERATOR, 8);
        put(TS.OUTPUT_OPERATOR, 9);
        put(TS.NEGATION, 10);
        put(TS.AND, 11);
        put(TS.OR, 12);
        put(TS.COMMA, 13);
        put(TS.EQUAL, 14);
        put(TS.INPUT_JOINT, 15);
        put(TS.OUTPUT_JOINT, 16);
        put(TS.COMPARE, 17);
        put(TS.COMPARE_NEGATION, 18);
        put(TS.MORE, 19);
        put(TS.LESS, 20);
        put(TS.MORE_OR_EQUAL, 21);
        put(TS.LESS_OR_EQUAL, 22);
//        put("^", 23);
        put(TS.ASTERISK, 24);
        put(TS.SLASH, 25);
        put(TS.PLUS, 26);
        put(TS.MINUS, 27);
        put(TS.OPENING_BRACKET, 28);
        put(TS.CLOSING_BRACKET, 29);
        put(TS.COLON, 30);
        put(TS.OPENING_BRACE, 31);
        put(TS.CLOSING_BRACE, 32);
        put(TS.SEMICOLON, 33);
        put(TS.UNDERLINE, 34);
        put(TS.EXCLAMATION, 35);
        put(TS.OPENING_SQUARE_BRACE, 36);
        put(TS.CLOSING_SQUARE_BRACE, 37);
        put(TS.END, 38);
        put(TS.LABEL, 100);
        put(TS.IDENTIFIER, 101);
        put(TS.CONSTANT, 102);
//        put("SW", 666);   // special word
    }
}
