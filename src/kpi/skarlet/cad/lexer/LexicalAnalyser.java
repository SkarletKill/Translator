package kpi.skarlet.cad.lexer;

import kpi.skarlet.cad.lexer.constants.CharacterConstants;
import kpi.skarlet.cad.lexer.constants.InitKeywords;
import kpi.skarlet.cad.lexer.constants.TerminalSymbols;
import kpi.skarlet.cad.lexer.exceptions.EndOfFileException;
import kpi.skarlet.cad.lexer.exceptions.LexicalException;
import kpi.skarlet.cad.lexer.exceptions.lexical.*;
import kpi.skarlet.cad.lexer.lexemes.*;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class LexicalAnalyser {
    private final String PATH_PROGRAM = "res/program.txt";
    private static CharacterConstants CC;
    private static TerminalSymbols TS;
    private static BufferedReader br;

    private final Map<String, Integer> keywords = initKeywords();

    private VariableType ACTIVE_TYPE;

    public static final int LBL_CODE = 100;
    public static final int IDN_CODE = 101;
    public static final int CON_CODE = 102;

    private List<Lexeme> lexemes = Lexeme.getList();
    private List<Label> labelList = Label.getList();
    private List<Identifier> identifierList = Identifier.getList();
    private List<Constant> constantList = Constant.getList();
    private List<LexicalException> exceptions = LexicalException.getList();

    private boolean lastConst = false;

    private int LINE_NUMBER = 1;
    private char ch;
    private String lex = "";
    private boolean HAS_TO_READ = true;


    private boolean hasFile = false;
    private String text = "";
    private int iterator;

    public LexicalAnalyser() {
        setTextFromFile();
    }

    public LexicalAnalyser(String text) {
        this.text = text;
    }

    public static String readFileAsString(String fileName) {
        String text = "";
        try {
            text = new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text;
    }

    public static void main(String[] args) {
        {
            LexicalAnalyser lexer = new LexicalAnalyser();
            lexer.run();
            System.out.println();
        }
//        {
//            LexicalAnalyser lexer = new LexicalAnalyser();
//            lexer.run("int input, res;\n" +
//                    "{\n" +
//                    "\tcin >> input;\n" +
//                    "\tif (input > 0) goto exit:;\n" +
//                    "\tcout << input;\n" +
//                    "\tres = -2.-3.-1.;\n" +
//                    "\tcout << res;\n" +
//                    "\texit:\n" +
//                    "}");
//            System.out.println();
//        }
    }

    public boolean run() {
        try {
            this.text += " ";
//            setTextFromFile();
            do {
                this.state1();
            } while (true);
        } catch (IOException e) {
            analyzeLabels();
            if (e instanceof EndOfFileException && exceptions.isEmpty()) return true;
            else return false;
        }
    }

    public boolean run(String text) {
        try {
            this.text = text;
            this.text += " ";
//            setTextFromFile();
            this.iterator = 0;

            do {
                this.state1();
            } while (true);
        } catch (IOException e) {
            analyzeLabels();
            if (e instanceof EndOfFileException && exceptions.isEmpty()) return true;
            else return false;
        }
    }

    public List<Lexeme> getLexemes() {
        return lexemes;
    }

    public List<Label> getLabels() {
        return labelList;
    }

    public List<Identifier> getIdentifiers() {
        return identifierList;
    }

    public List<Constant> getConstants() {
        return constantList;
    }

    public List<LexicalException> getExceptions() {
        return exceptions;
    }

    private char nextCharacter() throws IOException {
        int ch = br.read();
        if (ch == -1) {
            // closing the program
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            StackTraceElement place = stackTrace[2];
            System.out.println("Last state: " + place.getMethodName());
            if (exceptions.isEmpty())
                Lexeme.printTable();

            throw new EndOfFileException(place);
        }
        return this.ch = (char) ch;
    }

    private char nextChar() throws EOFException {
        if (iterator < text.length())
            return this.ch = text.charAt(iterator++);
        else {
            // closing the program
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            StackTraceElement place = stackTrace[2];
            System.out.println("Last state: " + place.getMethodName());
//            if (exceptions.isEmpty())
//                Lexeme.printTable();

            throw new EOFException();
        }
    }

    private void state1() throws IOException {
        clearLex();
        if (HAS_TO_READ) ch = nextChar();
//        if (Character.isJavaIdentifierStart(ch)) {
        if (Character.isLetter(ch)) {
            lex += ch;
            nextChar();
            state2();
        } else if (Character.isDigit(ch)) {
            lex += ch;
            nextChar();
            state3();
        } else if (ch == CC.dot) {
            lex += ch;
            nextChar();
            state4();
        } else if (ch == CC.equal) {
            lex += ch;
            nextChar();
            state6();
        } else if (ch == CC.exclamation) {
            lex += ch;
            nextChar();
            state7();
        } else if (ch == CC.more) {
            lex += ch;
            nextChar();
            state8();
        } else if (ch == CC.less) {
            lex += ch;
            nextChar();
            state9();
        } else if (ch == CC.minus) {
            lex += ch;
            nextChar();
            state10();
        } else if (isSingleCharacterSeparator(ch)) {
            lex += ch;
            state5();
        } else if (isWhiteSeparator(ch)) {
            if (ch == '\n') LINE_NUMBER++;
            HAS_TO_READ = true;
            // go to state 1
            return;
        } else {
            exceptions.add(new UnknownSymbolException(ch, LINE_NUMBER));
        }

    }

    private void state2() throws IOException {
        if (Character.isLetterOrDigit(ch)) {
            lex += ch;
            nextChar();
            state2();
        } else if (ch == CC.colon) {
            lex += ch;
            state11();
        } else {
            if (isKeyword(lex)) {
                if (getSpecialLexCode(lex) == 1) ACTIVE_TYPE = VariableType.INT;
                else if (getSpecialLexCode(lex) == 2) ACTIVE_TYPE = VariableType.FLOAT;
                addLex(lex, LexemeType.TERMINAL_SYMBOL);
            } else {
                checkIdentifier(lex);
                addLex(lex, LexemeType.IDENTIFIER);
            }
            HAS_TO_READ = false;
//            state1();
        }
    }

    private void state3() throws IOException {
        if (Character.isDigit(ch)) {
            lex += ch;
            nextChar();
            state3();
        } else if (ch == CC.dot) {
            lex += ch;
            nextChar();
            state12();
        } else {
            addLex(lex, LexemeType.CONSTANT);
            HAS_TO_READ = false;
//            state1();
        }
    }

    private void state4() throws IOException {
        if (Character.isDigit(ch)) {
            lex += ch;
            nextChar();
            state12();
        } else {
            exceptions.add(new UnexpectedLexemeException(lex, LINE_NUMBER));
        }
    }

    private void state5() {
        if (ch == CC.exclamation && ACTIVE_TYPE != null) ACTIVE_TYPE = null;
        addLex(lex, LexemeType.TERMINAL_SYMBOL); // OP
        HAS_TO_READ = true;
//        state1();
    }

    private void state6() {
        if (ch == CC.equal) {
            lex += ch;
            state13();  // ==
        } else {
            addLex(lex, LexemeType.TERMINAL_SYMBOL); // =
            HAS_TO_READ = false;
//            state1();
        }
    }

    private void state7() {
        if (ch == CC.equal) {
            lex += ch;
            state13();  // !=
        } else {
            if (ACTIVE_TYPE != null) ACTIVE_TYPE = null;
            addLex(lex, LexemeType.TERMINAL_SYMBOL); // OP
            HAS_TO_READ = false;
//            exceptions.add(new UnexpectedLexemeException(lex, LINE_NUMBER));
//            clearLex();
        }
    }

    private void state8() {
        if (ch == CC.more) {
            lex += ch;
            state13();  // >>
        } else if (ch == CC.equal) {
            lex += ch;
            state13();  // >=
        } else {
            addLex(lex, LexemeType.TERMINAL_SYMBOL); // >
            HAS_TO_READ = false;
//            state1();
        }
    }

    private void state9() {
        if (ch == CC.less) {
            lex += ch;
            state13();  // <<
        } else if (ch == CC.equal) {
            lex += ch;
            state13();  // <=
        } else {
            addLex(lex, LexemeType.TERMINAL_SYMBOL);     // <
            HAS_TO_READ = false;
//            state1();
        }
    }

    private void state10() throws IOException {
        if (Character.isDigit(ch) && !lastConst) {
            lex += ch;
            nextChar();
            state3();
        } else {
            addLex(lex, LexemeType.TERMINAL_SYMBOL);     // -
            HAS_TO_READ = false;
//            state1()
        }
    }

    private void state11() {
        addLex(lex, LexemeType.LABEL);
        HAS_TO_READ = true;
//        state1();
    }

    private void state12() throws IOException {
        if (Character.isDigit(ch)) {
            lex += ch;
            nextChar();
            state12();
        } else {
            addLex(lex, LexemeType.CONSTANT);
            HAS_TO_READ = false;
//            state1();
        }
    }

    private void state13() {
        addLex(lex, LexemeType.TERMINAL_SYMBOL);
        HAS_TO_READ = true;
//        state1();
    }

    private void clearLex() {
        lex = "";
    }

    private void addLex(String lex, LexemeType lexType) {
        Lexeme lexeme = null;

        if (lexType.equals(LexemeType.TERMINAL_SYMBOL)) {
            lexeme = new Lexeme(lex, LINE_NUMBER, getSpecialLexCode(lex));

        } else if (lexType.equals(LexemeType.LABEL)) {
            boolean hasGoto = (Lexeme.getLastCode() == 6);  // goto key code
            lexeme = new Lexeme(lex, LINE_NUMBER, LBL_CODE, getLabelCode(lex));

            if (!Label.isExists(lex)) {
                Label label = new Label(lex);
//                labelList.add(new Label(lex));
                labelList.add(label);
                if (hasGoto) label.setFrom(Lexeme.getList().size() - 1);
                else label.setTo(Lexeme.getList().size() - 1);
            } else {
                Label label = Label.get(lex);
                if (hasGoto && label.getFromIndex() == null) {
                    label.setFrom(Lexeme.getList().size() - 1);
                } else if (hasGoto && label.getFromIndex() != null) {
                    this.exceptions.add(new LabelRedeclarationException(lex, this.LINE_NUMBER));
                } else if (!hasGoto && label.getToIndex() == null) {
                    label.setTo(Lexeme.getList().size() - 1);
                } else if (!hasGoto && label.getToIndex() != null) {
                    this.exceptions.add(new LabelRecallException(lex, this.LINE_NUMBER));
                }
            }

        } else if (lexType.equals(LexemeType.IDENTIFIER)) {
            lexeme = new Lexeme(lex, LINE_NUMBER, IDN_CODE, getIdentifierCode(lex));
            if (!Identifier.isExists(lex)) identifierList.add(new Identifier(ACTIVE_TYPE, lex));

        } else if (lexType.equals(LexemeType.CONSTANT)) {
            lastConst = true;
            lexeme = new Lexeme(lex, LINE_NUMBER, CON_CODE, getConstantCode(lex));
            if (!Constant.isExists(lex)) constantList.add(new Constant(lex));
        } else {
//            throw new UnexpectedLexemeException(lex, LINE_NUMBER);
            System.err.println("ERROR: UNEXPECTED EVENT!");
        }

//        if (lexeme != null) lexemes.add(lexeme);
        checkLastConst();
    }

    private void checkLastConst() {
        int c = Lexeme.getLastCode();
        if (c == 102 || c == 29 || c == 101) lastConst = true;
        else lastConst = false;
    }

    private boolean isWhiteSeparator(char ch) {
        if (Character.isSpaceChar(ch)) return true;
        if (ch == '\t' || ch == '\r' || ch == '\n') return true;
        return false;
    }

    private boolean isSingleCharacterSeparator(char ch) {
        String regex = "[;,+*/=><{}()_\\[\\]]";   // need add [ ]
        String character = "" + ch;
        return character.matches(regex);
    }

    private boolean isKeyword(String lex) {
        return keywords.get(lex) != null;
    }

    // check if there is a lexeme in the list
    private boolean isExists(String lex, List<String> list) {
        return list.indexOf(lex) != -1;
    }

    private Integer getSpecialLexCode(String lex) {
        if (keywords.get(lex) == null)
            System.out.println();
        return keywords.get(lex);
    }

    // returns ordinal number (if exists) / 1 (if not exists) of lexeme from list
    private int getCode(List<String> list, String lex) {
        int code = list.indexOf(lex);
        if (code == -1) code = list.size();
        return code + 1;
    }

    private int getLabelCode(String lex) {
        return Label.getCode(lex);
    }

    private int getIdentifierCode(String lex) {
        return Identifier.getCode(lex);
    }

    private int getConstantCode(String lex) {
        return Constant.getCode(lex);
    }

    private void checkIdentifier(String idn) {
        if (ACTIVE_TYPE != null && Identifier.isExists(idn) && Identifier.get(idn).getType() != null) {
            exceptions.add(new IdentifierRedeclarationException(idn, LINE_NUMBER));
        } else if (ACTIVE_TYPE == null && !Identifier.isExists(idn))
            exceptions.add(new IdentifierUsingWithoutDeclarationException(idn, LINE_NUMBER));
//            else if (ACTIVE_TYPE == null && isExists(idn, identifierList)) // => Використання ідентифікатора
//            else if (ACTIVE_TYPE != null && !isExists(idn, identifierList)) // => Оголошення ідентифікатора
    }

    private void analyzeLabels() {
        for (Label label : labelList) {
            if (label.getFromIndex() == null)
                exceptions.add(new LabelUsingWithoutDeclarationException(label.getName(), Lexeme.get(label.getName()).getLine()));
//                exceptions.add(new LabelUsingWithoutDeclarationException(label.getName(), Lexeme.getList().get(label.getToIndex()).getLine()));

            if (label.getToIndex() == null)
                exceptions.add(new LabelTransitionException(label.getName(), Lexeme.get(label.getName()).getLine()));
//                exceptions.add(new LabelUsingWithoutDeclarationException(label.getName(), Lexeme.getList().get(label.getFromIndex()).getLine()));
        }
    }

    private void setTextFromFile() {
        this.text = readFileAsString(PATH_PROGRAM);
    }

    private static Map<String, Integer> initKeywords() {
        return new InitKeywords();
    }

    public Map<String, Integer> getKeywords() {
        return keywords;
    }

    public void clear() {
        lexemes.clear();
        identifierList.clear();
        constantList.clear();
        labelList.clear();
        exceptions.clear();

        text = "";
        LINE_NUMBER = 1;
        ACTIVE_TYPE = null;
        lastConst = false;
        hasFile = false;
        HAS_TO_READ = true;
    }

}