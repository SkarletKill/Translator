package kpi.skarlet.cad.lexer.exceptions.lexical;

import kpi.skarlet.cad.lexer.exceptions.LexicalException;

public class IdentifierRedeclarationException extends LexicalException {
    private String idn;

    public IdentifierRedeclarationException(String lex, int line) {
        this.idn = lex;
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
            msgBuilder.append("Identifier '")
                    .append(idn)
                    .append("' redeclaration");
            return msgBuilder.toString();
    }
}
