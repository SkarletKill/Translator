package kpi.skarlet.cad.poliz.performer;

import kpi.skarlet.cad.lexer.VariableType;
import kpi.skarlet.cad.lexer.constants.CharacterConstants;
import kpi.skarlet.cad.lexer.constants.InitKeywords;
import kpi.skarlet.cad.lexer.constants.TerminalSymbols;
import kpi.skarlet.cad.lexer.lexemes.Lexeme;
import kpi.skarlet.cad.poliz.CodeParser;
import kpi.skarlet.cad.poliz.constants.PolizConstants;
import kpi.skarlet.cad.poliz.memory.Label;
import kpi.skarlet.cad.poliz.memory.ProgramMemory;
import kpi.skarlet.cad.poliz.memory.Variable;
import kpi.skarlet.cad.poliz.performer.exceptions.RuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Performer {
    private final Map<String, Integer> keywords;
    private TerminalSymbols TS;
    private PolizConstants PC;
    private ProgramMemory memory;
    private Stack<Lexeme> stack;
    private int lastOperatorIdx = 0;    // index of last used operator
    private VariableType currentType;
    private List<Lexeme> lexemes;

    public Performer() {
        this.keywords = new InitKeywords();
        this.memory = new ProgramMemory();
        this.stack = new Stack<>();
        this.lexemes = new ArrayList<>();
    }

    public void clear() {
        this.memory.clear();
        this.stack.clear();
        this.lastOperatorIdx = 0;
    }

    public void perform(CodeParser parser) {
        this.mergeLabels(parser.getLabels());
        // Here mb sort labels
        this.lexemes.addAll(parser.getResult());
        this.setLabelsSpCode();
        this.handleAll(this.lexemes);
    }

    private void mergeLabels(List<Label> labels) {
        for (int i = 0; i < labels.size(); i++) {
            Label l = labels.get(i);
            if (l.getName().contains(String.valueOf(CharacterConstants.colon)))
                continue;
            Label label = new Label(l.getFromIdx(), l.getName(), l.getToIdx());

            for (Label l1 : labels) {
                if (l1.getName().charAt(l1.getName().length() - 1) == CharacterConstants.colon &&
                        l1.getName().substring(0, l1.getName().length() - 1).equals(l.getName())) {
                    if (!label.hasToIdx()) label.setToIdx(l1.getToIdx());
                    break;
                }
            }
            this.memory.getLabels().add(label);
        }
    }

    private void setLabelsSpCode() {
        for (Lexeme lexeme : lexemes) {
            if (lexeme.getCode() == getCode(TS.LABEL)) {
                if (lexeme.getName().contains(String.valueOf(TS.COLON))) {
                    lexeme.setName(lexeme.getName().substring(0, lexeme.getName().length() - 1));
                }
                List<Label> labels = memory.getLabels();
                for (int i = 0; i < labels.size(); i++) {
                    if (labels.get(i).getName().equals(lexeme.getName())) {
                        lexeme.setSpCode(i);
                        break;
                    }
                }
            }
        }
    }

    public void handleOne(Lexeme lexeme) {

    }

    public void handleAllDemo(List<Lexeme> lexemes) {
        for (Lexeme lexeme : lexemes) {
            handleOne(lexeme);
        }
    }

    public void handleAll(List<Lexeme> lexemes) {
        handleDeclarationList(lexemes);

        for (int i = lastOperatorIdx + 1; i < lexemes.size(); i++) {
            Lexeme lexeme = lexemes.get(i);
//            handleOne(lexeme);
            if (lexeme.getCode() == getCode(TS.IDENTIFIER) || lexeme.getCode() == getCode(TS.CONSTANT)
                    || lexeme.getCode() == getCode(TS.LABEL)) {
                stack.push(lexeme);
            } else if (lexeme.getCode() != PC.SP_CODE) {    // operator

            } else {    // lexeme.getCode() == PC.SP_CODE
                if (lexeme.getName().equals(PC.JMP)) {
                    int lblSpCode = stack.pop().getSpCodeLbl();
                    i = memory.getLabels().get(lblSpCode).getToIdx();
                } /*else if(){

                }*/
            }
        }
    }

    private void handleDeclarationList(List<Lexeme> lexemes) {
        int typeIndex = findFirstTypeIndex(lexemes, lastOperatorIdx + 1);
        if (typeIndex == -1) return;
        else {
            currentType = lexemes.get(typeIndex).getName().equals(PC.TYPE_INT) ? VariableType.INT : VariableType.FLOAT;
            for (int i = lastOperatorIdx; i < typeIndex; i++) {
                Lexeme lexeme = lexemes.get(i);
                if (lexeme.getCode() == getCode(TS.IDENTIFIER)) {
                    Variable variable = new Variable(lexeme.getName(), currentType);    // (currentType.equals(VariableType.INT)) ? VariableType.INT : VariableType.FLOAT;
                    this.memory.getVariables().add(variable);
                } else {
//                    throw new RuntimeException();
                    System.err.println("Expected only identifier, but found: lexCode = " + lexeme.getCode());
                }
            }
            this.lastOperatorIdx = typeIndex;
            handleDeclarationList(lexemes);
        }
    }

    private int findFirstTypeIndex(List<Lexeme> list, int start) {
        for (int i = start; i < list.size(); i++) {
            if (list.get(i).getName().equals(PC.TYPE_INT) || list.get(i).getName().equals(PC.TYPE_FLOAT))
                return i;
        }
        return -1;
    }

    private Integer getCode(String string) {
        return keywords.get(string);
    }
}
