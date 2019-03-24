package kpi.skarlet.cad.lexer.exceptions.lexical;

import kpi.skarlet.cad.lexer.exceptions.LexicalException;

public class UnexpectedLexemeException extends LexicalException {
    private String lexeme;

    public UnexpectedLexemeException(String lex, int line) {
        this.lexeme = lex;
        this.line = line;

        super.exclamation();
    }

    @Override
    public String getMessage() {
        StringBuilder msgBuilder = new StringBuilder();
        if (line != 0) msgBuilder.append("line ")
                .append(line)
                .append(": ");
        msgBuilder.append("Unexpected lexeme '")
                .append(lexeme)
                .append("'");
        return msgBuilder.toString();
    }
}
