package kpi.skarlet.cad.lexer.exceptions.lexical;

import kpi.skarlet.cad.lexer.exceptions.LexicalException;

public class UnknownLexemeTypeException extends LexicalException {
    private String lex;

    public UnknownLexemeTypeException(String lex, int line) {
        this.lex = lex;
        this.line = line;

        super.exclamation();
    }

    @Override
    public String getMessage() {
        StringBuilder msgBuilder = new StringBuilder();
        if (line != 0) msgBuilder.append("line ")
                .append(line)
                .append(": ");
        msgBuilder.append("Unknown type of lexeme '")
                .append(lex)
                .append("'");
        return msgBuilder.toString();
    }
}
