package kpi.skarlet.cad.lexer.exceptions;

import kpi.skarlet.cad.forwarding.exceptions.TranslatorException;

import java.util.ArrayList;
import java.util.List;

public abstract class LexicalException extends TranslatorException {
    private static List<LexicalException> lexicalExceptions = new ArrayList<>();
    protected int line;

    public LexicalException() {
//        lexicalExceptions.add(this);
    }

    protected void exclamation(){
        System.err.println(this.getMessage());
    }

    @Override
    public String getMessage() {
        String msg = "Lexical exception!";
        return msg;
    }

    public int getLine(){
        return this.line;
    }

    public static List<LexicalException> getList() {
        return lexicalExceptions;
    }

    public Integer getId() {
        return lexicalExceptions.indexOf(this) + 1;
    }
}
