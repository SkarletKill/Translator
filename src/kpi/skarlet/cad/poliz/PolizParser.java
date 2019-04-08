package kpi.skarlet.cad.poliz;

import kpi.skarlet.cad.lexer.lexemes.Lexeme;

import java.util.List;

public interface PolizParser {
    void parse(List<Lexeme> expression);
}
