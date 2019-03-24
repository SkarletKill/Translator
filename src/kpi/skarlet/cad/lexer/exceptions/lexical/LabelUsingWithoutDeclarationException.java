package kpi.skarlet.cad.lexer.exceptions.lexical;

import kpi.skarlet.cad.lexer.exceptions.LexicalException;

public class LabelUsingWithoutDeclarationException extends LexicalException {
    private String lbl;

    public LabelUsingWithoutDeclarationException(String lex, int line) {
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
        msgBuilder.append("Label '")
                .append(lbl)
                .append("' using without declaration");
        return msgBuilder.toString();
    }
}
