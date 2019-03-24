package kpi.skarlet.cad.lexer.exceptions.lexical;

import kpi.skarlet.cad.lexer.exceptions.LexicalException;

public class LabelRecallException extends LexicalException {
    private String lbl;

    public LabelRecallException(String lbl, int line) {
        this.lbl = lbl;
        this.line = line;

        super.exclamation();
    }

    @Override
    public String getMessage() {
        StringBuilder msgBuilder = new StringBuilder();
        if (line != 0) msgBuilder.append("line ")
                .append(line)
                .append(": ");
        msgBuilder.append("Ambiguous jump to label '")
                .append(lbl)
                .append("'");
        return msgBuilder.toString();
    }
}
