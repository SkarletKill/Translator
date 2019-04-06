package kpi.skarlet.cad.poliz;

import kpi.skarlet.cad.forwarding.constants.WordType;
import kpi.skarlet.cad.forwarding.grammar.Word;
import kpi.skarlet.cad.lexer.LexicalAnalyser;
import kpi.skarlet.cad.lexer.constants.TerminalSymbols;
import kpi.skarlet.cad.lexer.lexemes.Lexeme;
import kpi.skarlet.cad.poliz.entity.ForData;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class ArithmeticExpressionParser implements PParser {
    //    private final String PATH = "res/program.txt";
    private TerminalSymbols TS;
    private LexicalAnalyser lexer;
    private List<Lexeme> parsed;
    private Stack<Lexeme[]> stack;
    private Stack<ForData> forDataStack;
    private boolean underlineFirst = false;
    private  boolean forUL2Filter = false;
    private boolean outputChanged = false;
    private int lblIdx = 1;
    private final String JNE = "JNE";
    private final String JMP = "JMP";
    private final String LBL = "m";

    public ArithmeticExpressionParser(LexicalAnalyser lexer) {
        this.lexer = lexer;
        this.parsed = new ArrayList<>();
        this.stack = new Stack<>();
        this.forDataStack = new Stack<>();
    }

    public static void main(String[] args) {
//        LexicalAnalyser lexer = new LexicalAnalyser("int a, b, c, d, e, f! a = a + b * c - (d / e) / f * e");
        LexicalAnalyser lexer = new LexicalAnalyser("int a, b, c, d, e, f! a = 1 + 2 * 3 - (4 + 5) / 6 * 7");
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
            if (forUL2Filter) {
                // invocation target exception
                if (lexeme.getCode() != getCode(TS.CLOSING_SQUARE_BRACE)) {
                    forDataStack.peek().pushEndIter(lexeme);
                } else {
                    // here is a CLOSING_SQUARE_BRACE
                    parsed.add(new Lexeme(LBL + lblIdx++, lexeme.getLine(), getCode(TS.CYCLE_FOR), false));
                    parsed.add(new Lexeme(JNE, lexeme.getLine(), getCode(TS.CYCLE_FOR), false));
                    forUL2Filter = false;
                }
            } else {
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

                    } else if (lexCode == getCode(TS.END)) {
                        if (unwrap(stack.peek()).getCode() == getCode(TS.CONDITIONAL_OPERATOR)) {
                            Lexeme[] condFull = stack.pop();
                            parsed.add(new Lexeme(condFull[1].getName() + ":", condFull[0].getLine(), condFull[0].getCode(), false));
                            i++; // skip next semicolon
                        } else {
                            parsed.add(unwrap(stack.pop()));
                            outputChanged = true;
                            i--;
                        }
                    } /* else if (lexCode == getCode(TS.SEMICOLON)) {

                }*/ else {
                        // default scenario
                        // COMMA, COLON, UNDERLINE, EXCLAMATION, END
                        stack.push(wrap(lexeme));
                    }
                } else if (!stack.empty() && getPriority(unwrap(stack.peek())) >= lexPriority
                        && !isOpeningBracket(lexeme)) {
                    parsed.add(unwrap(stack.pop()));
                    outputChanged = true;
                    i--;
                } else {    // lexPriority != 99 && (stack is empty || stack priority less || lexeme is opening bracket)
                    if (isClosingBracket(lexeme)) {
                        if (stack.empty()) {
                            System.err.println("ArithmeticExpressionParser >> Stack is empty!!!");
                        }

                        if (unwrap(stack.peek()).getCode() == lexCode - 1)     // the same type of bracket
                            stack.pop();

                        if (lexCode == getCode(TS.CLOSING_BRACKET))
                            handleIfThen();
                        if (lexCode == getCode(TS.CLOSING_BRACE))
                            i += handleOpBlock();

                        continue;
                    }

                    if (lexCode == getCode(TS.CYCLE_FOR)) {
                        handleForStart(lexeme);
                    } else if (lexCode == getCode(TS.UNDERLINE)) {
                        underlineFirst = !underlineFirst;
                        if (underlineFirst) {
                            parsed.addAll(forDataStack.peek().popStart());
                            Lexeme[] lexemes = stack.peek();    // must be 'for'
                            if (unwrap(lexemes).getCode() == getCode(TS.CYCLE_FOR)) {   // what it is?
                                stack.pop();
                                Lexeme[] nLexemes = new Lexeme[]{lexemes[0],
                                        lexemes[1],
                                        new Lexeme(LBL + lblIdx, lexemes[0].getLine(), lexemes[0].getCode(), false)};
                                stack.push(nLexemes);
                            } else {
                                System.err.println("ArithmeticExpressionParser >> expected 'for' in stack, after first underline");
                            }
                        } else {    // second underline
                            forUL2Filter = true;
                            // else skip
                        }
                    } else {
                        if (lexCode == getCode(TS.SEMICOLON)) {     // semicolon - end for
                            if (unwrap(stack.peek()).getCode() == getCode(TS.CYCLE_FOR)) {
                                Lexeme[] stackFor = stack.pop();
                                parsed.add(stackFor[1]);
                                parsed.add(new Lexeme(JMP, stackFor[0].getLine(), stackFor[0].getCode(), false));
                                parsed.add(new Lexeme(stackFor[2].getName() + ":", stackFor[2].getLine(), stackFor[2].getCode(), false));
//                                stack.push(wrap(lexeme));     // need or not?
                            } else
                                continue;
                        } else if (isOnlySyntaxLexeme(lexCode)) {
                            // empty body
                        } else {
                            stack.push(wrap(lexeme));
                        }
                    }

                }
            }
        }

//        releaseRecollection();
    }

    private void releaseRecollection() {
        while (!stack.empty()) {
            Lexeme lexeme = unwrap(stack.pop());
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

    private boolean isOnlySyntaxLexeme(int lexCode) {
        return lexCode == getCode(TS.OPENING_SQUARE_BRACE);
    }

    private Integer getCode(String string) {
        return lexer.getKeywords().get(string);
    }

    private int getPriority(Lexeme lexeme) {
        switch (lexeme.getCode()) {
            case 31:
                return -1;
            case 28:    // (
//            case 31:    // {
            case 36:    // [
            case 3:     // for
            case 7:     // if
                return 0;
            case 29:    // )
            case 32:    // }
            case 37:    // ]
            case 33:    // ;
            case 34:    // _
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

    private Lexeme[] wrap(Lexeme... lexs) {
        return lexs;
    }

    private Lexeme unwrap(Lexeme[] lexs) {
        if (lexs.length < 2)
            return lexs[0];
        else {
            System.err.println("Unexpected situation 'unwrap' (complex)");
            return lexs[0];
        }
    }

    private void handleIfThen() {
        Lexeme[] lexemes = stack.peek();
        if (!lexemes[0].getName().equals(TS.CONDITIONAL_OPERATOR)) {
            System.err.println("ArithmeticExpressionParser >> Expected 'if' but found " + lexemes[0].getName());
        } else {
            parsed.add(new Lexeme(LBL + lblIdx, lexemes[0].getCode(), getCode(TS.CONDITIONAL_OPERATOR), false));
            parsed.add(new Lexeme(JNE, lexemes[0].getCode(), getCode(TS.CONDITIONAL_OPERATOR), false));

            // !!! add lbl to label table

            stack.pop();
            Lexeme[] nLexemes = new Lexeme[]{lexemes[0],
                    new Lexeme(LBL + lblIdx++, lexemes[0].getLine(), lexemes[0].getCode(), false)};
            stack.push(nLexemes);
        }
    }

    // return needed change of iterator
    private int handleOpBlock() {
        // !!! not tested
        Lexeme[] lexemes = stack.peek();
        if (lexemes[0].getName().equals(TS.CONDITIONAL_OPERATOR)) {
            // analogue to END ?
            if (unwrap(stack.peek()).getCode() == getCode(TS.CONDITIONAL_OPERATOR)) {
                Lexeme[] condFull = stack.pop();
                parsed.add(new Lexeme(condFull[1].getName() + ":", condFull[0].getLine(), condFull[0].getCode(), false));
                return 1; // skip next semicolon
            } else {
                parsed.add(unwrap(stack.pop()));
                outputChanged = true;
                return -1;
            }
        } else if (lexemes[0].getName().equals(TS.CYCLE_FOR)) {

            return 0;
        } else {

            return 0;
        }
    }

    private void handleForStart(Lexeme lexeme) {
        ForData forData = new ForData();
        forData.pushStart(new Lexeme(LBL + lblIdx + ":", lexeme.getCode(), getCode(TS.CYCLE_FOR), false));
        forDataStack.push(forData);
        Lexeme[] nLexemes = new Lexeme[]{lexeme,
                new Lexeme(LBL + lblIdx++, lexeme.getLine(), lexeme.getCode(), false)};
        stack.push(nLexemes);
    }
}
