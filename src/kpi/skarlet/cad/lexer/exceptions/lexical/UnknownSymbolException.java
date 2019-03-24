package kpi.skarlet.cad.lexer.exceptions.lexical;

import kpi.skarlet.cad.lexer.exceptions.LexicalException;

public class UnknownSymbolException extends LexicalException {
    private char symbol;

    public UnknownSymbolException(char ch, int line) {
        this.symbol = ch;
        this.line = line;

        super.exclamation();
    }

    @Override
    public String getMessage() {
        StringBuilder msgBuilder = new StringBuilder();
        if (line != 0) msgBuilder.append("line ")
                .append(line)
                .append(": ");
        msgBuilder.append("Unexpected character '")
                .append(symbol)
                .append("'");
        return msgBuilder.toString();
    }
}
