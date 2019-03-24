package kpi.skarlet.cad.poliz;

import kpi.skarlet.cad.lexer.LexicalAnalyser;
import kpi.skarlet.cad.lexer.constants.TerminalSymbols;
import kpi.skarlet.cad.lexer.lexemes.Lexeme;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ArithmeticExpressionParser implements PParser {
    //    private final String PATH = "res/program.txt";
    private TerminalSymbols TS;
    private LexicalAnalyser lexer;
    private List<Lexeme> parsed;
    private Stack<Lexeme> stack;

    public ArithmeticExpressionParser(LexicalAnalyser lexer) {
        this.lexer = lexer;
        this.parsed = new ArrayList<>();
        this.stack = new Stack<>();
    }

    public static void main(String[] args) {
        LexicalAnalyser lexer = new LexicalAnalyser("int a, b, c, d, e, f! a = a + b * c ^ (d / e) / f");
        ArithmeticExpressionParser parser = new ArithmeticExpressionParser(lexer);
        parser.getLexer().run();
        parser.parse(lexer.getLexemes().subList(15, lexer.getLexemes().size()));
        System.out.println(parser.parsed);
    }

    public void parse(List<Lexeme> expression) {
        for (int i = 0; i < expression.size(); i++) {
            Lexeme lexeme = expression.get(i);
            if (lexeme.getCode() == getCode(TS.IDENTIFIER) || lexeme.getCode() == getCode(TS.CONSTANT)) {
                parsed.add(lexeme);
            } else if (!stack.empty() && getPriority(stack.peek()) >= getPriority(lexeme)
                    && !lexeme.getName().equals(TS.OPENING_BRACKET)) {
                parsed.add(stack.pop());
                i--;
            } else {
                if (lexeme.getName().equals(TS.CLOSING_BRACKET)) {
                    stack.pop();    // stack.peek() == '('
                    continue;
                }
                stack.push(lexeme);
            }
        }

        while (!stack.empty()) {
            Lexeme lexeme = stack.pop();
            if (lexeme.getName().equals(TS.OPENING_BRACKET)) continue;
            parsed.add(lexeme);
        }
    }

    private Integer getCode(String string) {
        return lexer.getKeywords().get(string);
    }

    private int getPriority(Lexeme lexeme) {
        switch (lexeme.getCode()) {
            case 28:    // (
                return 0;
            case 26:    // +
            case 27:    // -
            case 29:    // )
                return 1;
            case 24:    // *
            case 25:    // /
//            case ??:    // @
                return 2;
            case 23:
                return 3;
            default:
                return 99;
        }
    }

    public LexicalAnalyser getLexer() {
        return lexer;
    }
}
