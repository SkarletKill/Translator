package kpi.skarlet.cad.poliz;

import kpi.skarlet.cad.lexer.lexemes.Lexeme;

import java.util.List;

public interface PParser {
    void parse(List<Lexeme> expression);
}
