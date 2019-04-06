package kpi.skarlet.cad.poliz.entity;

import kpi.skarlet.cad.lexer.lexemes.Lexeme;

import java.util.ArrayList;
import java.util.List;

public class ForData {
    private List<Lexeme> start;
    private List<Lexeme> endIter;

    public ForData() {
        this.start = new ArrayList<>();
        this.endIter = new ArrayList<>();
    }

    public List<Lexeme> getStart() {
        return start;
    }

    public List<Lexeme> getEndIter() {
        return endIter;
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
        this.endIter.add(endIter);
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
