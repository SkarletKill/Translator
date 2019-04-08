package kpi.skarlet.cad.poliz;

import kpi.skarlet.cad.forwarding.analyser.TableElem;
import kpi.skarlet.cad.forwarding.constants.WordType;
import kpi.skarlet.cad.forwarding.grammar.Word;
import kpi.skarlet.cad.lexer.LexicalAnalyser;
import kpi.skarlet.cad.lexer.constants.TerminalSymbols;
import kpi.skarlet.cad.lexer.lexemes.Lexeme;
import kpi.skarlet.cad.poliz.constants.PolizConstants;
import kpi.skarlet.cad.poliz.entity.ForData;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class CodeParser implements PolizParser {
    //    private final String PATH = "res/program.txt";
    private TerminalSymbols TS;
    private PolizConstants PC;
    private LexicalAnalyser lexer;
    private List<Lexeme> parsed;            // result POLIZ
    private Stack<Lexeme[]> stack;
    private Stack<ForData> forDataStack;    // structure for remember parts of 'for' cycle to using ones after
    private boolean underlineFirst = false; // underline as separator in 'for' cycle
    private boolean forUL2Filter = false;   // filter after second underline in 'for' (using for move incrementation to end of operator block)
    private Lexeme type = null;             // filter after type (using for doing specific act)
    private boolean gotoMemory = false;     // last lexeme was goto
    private Boolean ioStream = null;        // input - true, output - false
    private boolean outputChanged = false;
    private int lblIdx;                     // initialization in constructor
    private int lblCode = 100;
    private int spCode = 666;   // code for special lexemes, generated only in POLIZ parser (JNE, JMP, etc.)

    public CodeParser(LexicalAnalyser lexer) {
        this.lexer = lexer;
        this.parsed = new ArrayList<>();
        this.stack = new Stack<>();
        this.forDataStack = new Stack<>();
        TableElem.clear();
    }

    public static void main(String[] args) {
//        LexicalAnalyser lexer = new LexicalAnalyser("int a, b, c, d, e, f! a = a + b * c - (d / e) / f * e");
        LexicalAnalyser lexer = new LexicalAnalyser("int a, b, c, d, e, f! a = 1 + 2 * 3 - (4 + 5) / 6 * 7");
        CodeParser parser = new CodeParser(lexer);
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
                filtrationCycleAfterUL2(lexeme);
            } else {
                int lexCode = lexeme.getCode();
                int lexPriority = getPriority(lexeme);

                if (lexCode == getCode(TS.IDENTIFIER) || lexCode == getCode(TS.CONSTANT)) {
                    parsed.add(lexeme);
                    outputChanged = true;
                } else if (lexPriority == 99) {
                    // it's a keyword
                    if (lexCode == getCode(TS.TYPE_INT)) {
                        this.type = new Lexeme(PC.TYPE_INT, lexeme.getLine(), spCode, false);
                    } else if (lexCode == getCode(TS.TYPE_FLOAT)) {
                        this.type = new Lexeme(PC.TYPE_FLOAT, lexeme.getLine(), spCode, false);
                    } else if (lexCode == getCode(TS.EXCLAMATION)) {
                        parsed.add(type);
                        type = null;
                    } else if (lexCode == getCode(TS.LABEL_START)) {
                        gotoMemory = true;
                    } else if (lexCode == getCode(TS.LABEL)) {
                        if (gotoMemory) {
                            parsed.add(new Lexeme(PC.LBL + lexeme.getSpCode(), lexeme.getLine(), lexCode, false));
                            parsed.add(new Lexeme(PC.JMP, lexeme.getLine(), spCode, false));
                            gotoMemory = false;
                        } else {
                            parsed.add(new Lexeme(PC.LBL + lexeme.getSpCode() + TS.COLON, lexeme.getLine(), lexCode, false));
                        }
                    } else if (lexCode == getCode(TS.INPUT_OPERATOR)) {
                        ioStream = true;
                    } else if (lexCode == getCode(TS.INPUT_JOINT)) {

                    } else if (lexCode == getCode(TS.OUTPUT_OPERATOR)) {
                        ioStream = false;
                    } else if (lexCode == getCode(TS.OUTPUT_JOINT)) {

                    } else if (lexCode == getCode(TS.END)) {
                        if (ioStream != null) handleInputOutput(lexeme);    // end - end of cin/cout
                        if (unwrap(stack.peek()).getCode() == getCode(TS.CONDITIONAL_OPERATOR)) {
                            Lexeme[] condFull = stack.pop();
                            parsed.add(new Lexeme(condFull[1].getName() + TS.COLON, condFull[0].getLine(), lblCode, false));
                            i++; // skip next semicolon
                        } else {
                            parsed.add(unwrap(stack.pop()));
                            outputChanged = true;
                            i--;
                        }
                    } else {
                        // default scenario
                        // COMMA, COLON, UNDERLINE?
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
                            System.err.println("CodeParser >> Stack is empty!!!");
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
                        handleCycleSeparator();     // handle underline
                    } else {
                        if (lexCode == getCode(TS.SEMICOLON)) {     // semicolon - end for
                            if (ioStream != null) handleInputOutput(lexeme);    // semicolon - end of cin/cout
                            else if (unwrap(stack.peek()).getCode() == getCode(TS.CYCLE_FOR)) {
                                Lexeme[] stackFor = stack.pop();
                                parsed.add(stackFor[1]);
                                parsed.add(new Lexeme(PC.JMP, stackFor[0].getLine(), spCode, false));
                                parsed.add(new Lexeme(stackFor[2].getName() + ":", stackFor[2].getLine(), lblCode, false));
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

    // return needed change of iterator
    private int handleOpBlock() {
        // !!! not tested
        Lexeme[] lexemes = stack.peek();
        if (lexemes[0].getName().equals(TS.CONDITIONAL_OPERATOR)) {
            // analogue to END ?
            if (unwrap(stack.peek()).getCode() == getCode(TS.CONDITIONAL_OPERATOR)) {
                Lexeme[] condFull = stack.pop();
                parsed.add(new Lexeme(condFull[1].getName() + ":", condFull[0].getLine(), lblCode, false));
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

    private void handleIfThen() {
        Lexeme[] lexemes = stack.peek();
        if (!lexemes[0].getName().equals(TS.CONDITIONAL_OPERATOR)) {
            System.err.println("CodeParser >> Expected 'if' but found " + lexemes[0].getName());
        } else {
            parsed.add(new Lexeme(PC.LBL + lblIdx, lexemes[0].getLine(), getCode(TS.CONDITIONAL_OPERATOR), false));
            parsed.add(new Lexeme(PC.JNE, lexemes[0].getLine(), spCode, false));

            // !!! add lbl to label table

            stack.pop();
            Lexeme[] nLexemes = new Lexeme[]{lexemes[0],
                    new Lexeme(PC.LBL + lblIdx++, lexemes[0].getLine(), lexemes[0].getCode(), false)};
            stack.push(nLexemes);
        }
    }

    private void handleForStart(Lexeme lexeme) {
        ForData forData = new ForData();
        forData.pushStart(new Lexeme(PC.LBL + lblIdx + ":", lexeme.getCode(), lblCode, false));
        forDataStack.push(forData);
        Lexeme[] nLexemes = new Lexeme[]{lexeme,
                new Lexeme(PC.LBL + lblIdx++, lexeme.getLine(), lexeme.getCode(), false)};
        stack.push(nLexemes);
    }

    private void handleCycleSeparator() {
        underlineFirst = !underlineFirst;
        if (underlineFirst) {
            parsed.addAll(forDataStack.peek().popStart());
            Lexeme[] lexemes = stack.peek();    // must be 'for'
            if (unwrap(lexemes).getCode() == getCode(TS.CYCLE_FOR)) {   // what it is?
                stack.pop();
                Lexeme[] nLexemes = new Lexeme[]{lexemes[0],
                        lexemes[1],
                        new Lexeme(PC.LBL + lblIdx, lexemes[0].getLine(), lexemes[0].getCode(), false)};
                stack.push(nLexemes);
            } else {
                System.err.println("CodeParser >> expected 'for' in stack, after first underline");
            }
        } else {    // second underline
            forUL2Filter = true;
            // else skip
        }
    }

    private void handleInputOutput(Lexeme lexeme) {
        parsed.add(new Lexeme((ioStream) ? PC.INPUT : PC.OUTPUT, lexeme.getLine(), spCode, false));
        ioStream = null;
    }

    private void filtrationCycleAfterUL2(Lexeme lexeme) {
        // invocation target exception
        if (lexeme.getCode() != getCode(TS.CLOSING_SQUARE_BRACE)) {
            forDataStack.peek().pushEndIter(lexeme);
        } else {
            // here is a CLOSING_SQUARE_BRACE
            parsed.add(new Lexeme(PC.LBL + lblIdx++, lexeme.getLine(), getCode(TS.CYCLE_FOR), false));
            parsed.add(new Lexeme(PC.JNE, lexeme.getLine(), spCode, false));
            forUL2Filter = false;
        }
    }

    public void countLabels() {
        this.lblIdx = this.lexer.getLabels().size() + 1;
    }

    public void clear() {
        this.parsed.clear();
        this.stack.clear();
        this.forDataStack.clear();
    }
}
