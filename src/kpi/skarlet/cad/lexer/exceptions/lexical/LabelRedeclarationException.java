package kpi.skarlet.cad.lexer.exceptions.lexical;

import kpi.skarlet.cad.lexer.exceptions.LexicalException;

public class LabelRedeclarationException extends LexicalException {
    private String lbl;

    public LabelRedeclarationException(String lex, int line) {
        this.lbl = lex;
        this.line = line;

        super.exclamation();
    }

    @Override
    public int getLine() {
        return super.getLine();
    }

    @Override
    public String getMessage() {
        StringBuilder msgBuilder = new StringBuilder();
        if (line != 0) msgBuilder.append("line ")
                .append(line)
                .append(": ");
        msgBuilder.append("Label '")
                .append(lbl)
                .append("' redeclaration");
        return msgBuilder.toString();
    }
}
