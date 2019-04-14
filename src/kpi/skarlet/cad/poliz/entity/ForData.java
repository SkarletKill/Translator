package kpi.skarlet.cad.poliz.entity;

import kpi.skarlet.cad.lexer.LexicalAnalyser;
import kpi.skarlet.cad.lexer.lexemes.Lexeme;
import kpi.skarlet.cad.poliz.CodeParser;

import java.util.ArrayList;
import java.util.List;

public class ForData {
    private LexicalAnalyser lexer;
    private Lexeme parameter;
    private List<Lexeme> start;
    private List<Lexeme> endIter;

    public ForData(LexicalAnalyser lexer) {
        this.lexer = lexer;
        this.start = new ArrayList<>();
        this.endIter = new ArrayList<>();
    }

    public Lexeme getParameter() {
        return parameter;
    }

    public List<Lexeme> getStart() {
        return start;
    }

    public List<Lexeme> getEndIter() {
        parseEndIter();
        return endIter;
    }

    public void setParameter(Lexeme parameter) {
        this.parameter = new Lexeme(parameter.getName(), parameter.getLine(), parameter.getCode(), parameter.getSpCode(), false);
    }

    public void setStart(List<Lexeme> start) {
        this.start = start;
    }

    public void setEndIter(List<Lexeme> endIter) {
        this.endIter = endIter;
    }

    public List<Lexeme> popStart() {
        List<Lexeme> startClone = start;
        start = null;
        return startClone;
    }

    public List<Lexeme> popEndIter() {
        List<Lexeme> endIterClone = endIter;
        endIter = null;
        return endIterClone;
    }

    public void pushStart(Lexeme start) {
        this.start.add(start);
    }

    public void pushEndIter(Lexeme endIter) {
        if (this.parameter != null) {
            this.endIter.add(parameter);
            this.parameter = null;
            // code of '=' => 14
            this.endIter.add(new Lexeme("=", endIter.getLine(), 14, false));
        }
        this.endIter.add(endIter);
    }

    private void parseEndIter() {
        CodeParser parser = new CodeParser(this.lexer);
        parser.parse(this.endIter);
        parser.releaseRecollection();
        this.endIter = parser.getResult();
    }

    public boolean isEmpty() {
        return start.isEmpty() && endIter.isEmpty();
    }

    public boolean isFull() {
        return !start.isEmpty() && !endIter.isEmpty();
    }

    public boolean hasStart() {
        return !start.isEmpty();
    }

    public boolean hasEndIter() {
        return !endIter.isEmpty();
    }
}
