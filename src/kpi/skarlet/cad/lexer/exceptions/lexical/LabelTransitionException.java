package kpi.skarlet.cad.lexer.exceptions.lexical;

import kpi.skarlet.cad.lexer.exceptions.LexicalException;

public class LabelTransitionException extends LexicalException {
    private String lbl;

    public LabelTransitionException(String lex, int line) {
        this.lbl = lex;
        this.line = line;

        super.exclamation();
    }

    @Override
    public String getMessage() {
        StringBuilder msgBuilder = new StringBuilder();
        if (line != 0) msgBuilder.append("line ")
                .append(line)
                .append(": ");
        msgBuilder.append("No transition to the label '")
                .append(lbl)
                .append("'");
        return msgBuilder.toString();
    }
}
