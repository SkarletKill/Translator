package kpi.skarlet.cad.poliz;

import kpi.skarlet.cad.forwarding.constants.WordType;
import kpi.skarlet.cad.forwarding.grammar.Word;
import kpi.skarlet.cad.lexer.LexicalAnalyser;
import kpi.skarlet.cad.lexer.constants.InitKeywords;
import kpi.skarlet.cad.lexer.constants.TerminalSymbols;
import kpi.skarlet.cad.lexer.lexemes.Lexeme;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class ArithmeticExpressionParser implements PParser {
    //    private final String PATH = "res/program.txt";
    private TerminalSymbols TS;
    private LexicalAnalyser lexer;
    private List<Lexeme> parsed;
    private Stack<Lexeme> stack;
    private boolean outputChanged = false;

    public ArithmeticExpressionParser(LexicalAnalyser lexer) {
        this.lexer = lexer;
        this.parsed = new ArrayList<>();
        this.stack = new Stack<>();
    }

    public static void main(String[] args) {
//        LexicalAnalyser lexer = new LexicalAnalyser("int a, b, c, d, e, f! a = a + b * c - (d / e) / f * e");
        LexicalAnalyser lexer = new LexicalAnalyser("int a, b, c, d, e, f! a = 1 + 2 * 3 - (4 / 5) / 6 * 7");
        ArithmeticExpressionParser parser = new ArithmeticExpressionParser(lexer);
        parser.getLexer().run();
        parser.parse(lexer.getLexemes().subList(15, lexer.getLexemes().size() - 2));
        System.out.println(parser.parsed);
        parser.add(lexer.getLexemes().get(lexer.getLexemes().size() - 2));
        parser.add(lexer.getLexemes().get(lexer.getLexemes().size() - 1));
        parser.releaseRecollection();
        System.out.println(parser.parsed);
    }

    public void parse(List<Lexeme> expression) {
        this.outputChanged = false;
        for (int i = 0; i < expression.size(); i++) {
            Lexeme lexeme = expression.get(i);
            int lexCode = lexeme.getCode();
            int lexPriority = getPriority(lexeme);

            if (lexCode == getCode(TS.IDENTIFIER) || lexCode == getCode(TS.CONSTANT)) {
                parsed.add(lexeme);
                outputChanged = true;
            } else if (lexPriority == 99) {
                // it's a keyword
                if (lexCode == getCode(TS.TYPE_INT) || lexCode == getCode(TS.TYPE_FLOAT)) {

                } else if (lexCode == getCode(TS.LABEL_START)) {

                } else if (lexCode == getCode(TS.LABEL)) {

                } else if (lexCode == getCode(TS.INPUT_OPERATOR)) {

                } else if (lexCode == getCode(TS.INPUT_JOINT)) {

                } else if (lexCode == getCode(TS.OUTPUT_OPERATOR)) {

                } else if (lexCode == getCode(TS.OUTPUT_JOINT)) {

                } else if (lexCode == getCode(TS.CYCLE_FOR)) {

                } else if (lexCode == getCode(TS.CONDITIONAL_OPERATOR)) {

                } else if (lexCode == getCode(TS.SEMICOLON)) {

                } else {
                    // default scenario
                    // COMMA, COLON, UNDERLINE, EXCLAMATION, END
                }
            } else if (!stack.empty() && getPriority(stack.peek()) >= lexPriority
                    && !isOpeningBracket(lexeme)) {
                parsed.add(stack.pop());
                outputChanged = true;
                i--;
            } else {    // lexPriority != 99 && (stack is empty || stack priority less || lexeme is opening bracket)
                if (isClosingBracket(lexeme)) {
                    if (stack.empty()) {
                        System.err.println("ArithmeticExpressionParser >> Stack is empty!!!");
                    }

                    if (stack.peek().getCode() == lexCode - 1)     // the same type of bracket
                        stack.pop();
                    continue;
                }
                stack.push(lexeme);
            }
        }

//        releaseRecollection();
    }

    private void releaseRecollection() {
        while (!stack.empty()) {
            Lexeme lexeme = stack.pop();
            if (lexeme.getName().equals(TS.OPENING_BRACKET)) continue;
            parsed.add(lexeme);
        }
    }

    public boolean add(Lexeme lexeme) {
        List<Lexeme> lexList = new ArrayList<>();
        lexList.add(lexeme);
        parse(lexList);
        return this.outputChanged;
    }

    private boolean isOpeningBracket(Lexeme lexeme) {
        return lexeme.getCode() == getCode(TS.OPENING_BRACKET)
                || lexeme.getCode() == getCode(TS.OPENING_SQUARE_BRACE)
                || lexeme.getCode() == getCode(TS.OPENING_BRACE);
    }

    private boolean isClosingBracket(Lexeme lexeme) {
        return lexeme.getCode() == getCode(TS.CLOSING_BRACKET)
                || lexeme.getCode() == getCode(TS.CLOSING_SQUARE_BRACE)
                || lexeme.getCode() == getCode(TS.CLOSING_BRACE);
    }

    private Integer getCode(String string) {
        return lexer.getKeywords().get(string);
    }

    private int getPriority(Lexeme lexeme) {
        switch (lexeme.getCode()) {
            case 28:    // (
            case 31:    // {
            case 36:    // [
                return 0;
            case 29:    // )
            case 32:    // }
            case 37:    // ]
                return 1;
            case 14:    // =
                return 2;
            case 12:    // or
                return 3;
            case 11:    // and
                return 4;
            case 10:    // not
                return 5;
            case 17:    // ==
            case 18:    // !=
            case 19:    // >
            case 20:    // <
            case 21:    // >=
            case 22:    // <=
                return 6;
            case 26:    // +
            case 27:    // -
                return 7;
            case 24:    // *
            case 25:    // /
//            case ??:    // @
                return 8;
            case 23:    // unnecessary
                return 9;
            default:
                return 99;
        }
    }

    public List<Word> getStackCopyAsWords() {
        List<Word> collect = parsed.stream().map(l -> new Word(l.getName(), WordType.TERMINAL)).collect(Collectors.toCollection(ArrayList::new));
        return collect;
    }

    public LexicalAnalyser getLexer() {
        return lexer;
    }
}
