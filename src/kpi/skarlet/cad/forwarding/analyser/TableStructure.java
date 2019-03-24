package kpi.skarlet.cad.forwarding.analyser;

import kpi.skarlet.cad.forwarding.Signs;
import kpi.skarlet.cad.forwarding.constants.WordType;
import kpi.skarlet.cad.forwarding.grammar.Word;
import kpi.skarlet.cad.lexer.lexemes.Lexeme;

import java.util.List;

public class TableStructure {
    private List<Word> stack;
    private Signs ratio;
    private List<Lexeme> input;

    public TableStructure(List<Word> stack, Signs ratio, List<Lexeme> input) {
        this.stack = stack;
        this.ratio = ratio;
        this.input = input;
    }

    public TableStructure(List<Word> stack, List<Lexeme> input) {
        this.stack = stack;
        this.input = input;
    }

    public List<Word> getStack() {
        return stack;
    }

    public Word getStackLast() {
        return (stack.isEmpty()) ? null : stack.get(stack.size() - 1);
    }

    public Signs getRatio() {
        return ratio;
    }

    public List<Lexeme> getInput() {
        return input;
    }

    public Lexeme getInputFirst() {
        return (input.isEmpty()) ? null : input.get(0);
    }

    public Word getInputFirstWord() {
        if (input.isEmpty()) {
            System.err.println("getInputFirstWord() => Input empty!");
            return null;
        }
        int lexCode = input.get(0).getCode();

        if (lexCode == Sharp.code()) {
            return new Word(Sharp.name(), WordType.TERMINAL);
        } else if (lexCode == 100) {
            return new Word("label", WordType.TERMINAL);
        } else if (lexCode == 101) {
            return new Word("identifier", WordType.TERMINAL);
        } else if (lexCode == 102) {
            return new Word("constant", WordType.TERMINAL);
        } else return new Word(input.get(0).getName(), WordType.TERMINAL);

    }

    public void setRatio(Signs ratio) {
        this.ratio = ratio;
    }

    public void cutStackTail(int indexLast) {
        stack = stack.subList(0, indexLast);
    }

    public boolean moveWordFromInputToStack() {
        Word word = getInputFirstWord();
        if (word == null || this.input.isEmpty()) return false;
        this.stack.add(word);
        this.input = this.input.subList(1, input.size());
        return true;
    }

    public void clear() {
        this.stack.clear();
        this.input.clear();
        this.ratio = null;
    }

    @Override
    public String toString() {
        return "TableStructure{" +
                "stack=" + stack +
                ", ratio=" + ratio +
                ", input=" + input +
                '}';
    }
}
