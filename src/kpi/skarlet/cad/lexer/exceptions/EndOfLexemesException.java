package kpi.skarlet.cad.lexer.exceptions;

import kpi.skarlet.cad.forwarding.exceptions.TranslatorException;

public class EndOfLexemesException extends TranslatorException {
    private String message = "End of lexemes reached!";

    public EndOfLexemesException() {}

    @Override
    public String getMessage() {
        return message;
    }
}
