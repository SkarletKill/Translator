package kpi.skarlet.cad.poliz.performer;

import kpi.skarlet.cad.controller.Controller;
import kpi.skarlet.cad.lexer.VariableType;
import kpi.skarlet.cad.lexer.constants.CharacterConstants;
import kpi.skarlet.cad.lexer.constants.InitKeywords;
import kpi.skarlet.cad.lexer.constants.TerminalSymbols;
import kpi.skarlet.cad.lexer.lexemes.Lexeme;
import kpi.skarlet.cad.poliz.CodeParser;
import kpi.skarlet.cad.poliz.constants.PolizConstants;
import kpi.skarlet.cad.poliz.memory.Constant;
import kpi.skarlet.cad.poliz.memory.Label;
import kpi.skarlet.cad.poliz.memory.ProgramMemory;
import kpi.skarlet.cad.poliz.memory.Variable;
import kpi.skarlet.cad.poliz.performer.exceptions.RuntimeExceptions;
import kpi.skarlet.cad.poliz.performer.exceptions.runtime.VariableUsingException;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

public class Performer {
    private Controller controller;

    private final Map<String, Integer> keywords;
    private TerminalSymbols TS;
    private PolizConstants PC;
    private ProgramMemory memory;
    private Stack<Lexeme> stack;
    private VariableType currentType;
    private List<Lexeme> lexemes;
    private int lastOperatorIdx = 0;    // index of last used operator
    private int conLimit = -1;          // index of last constant generated by Lexer
    public boolean firstStart = true;

    public Performer() {
        this.keywords = new InitKeywords();
        this.memory = new ProgramMemory();
        this.stack = new Stack<>();
        this.lexemes = new ArrayList<>();
    }

    public void clear() {
        this.memory.clear();
        this.stack.clear();
        this.lexemes.clear();
        this.lastOperatorIdx = 0;
        this.conLimit = -1;
    }

    public void perform(Controller controller, CodeParser parser) {
        this.clear();
        this.controller = controller;

        this.rewriteConstants(parser.getLexer().getConstants());
        this.mergeLabels(parser.getLabels());
        // Here mb sort labels
        this.lexemes.addAll(parser.getResult());
        if (firstStart) {
            this.setLabelsSpCode();
            this.setVarSpCode();
            firstStart = false;
        }
        try {
            this.handleAll(this.lexemes);
        } catch (RuntimeExceptions ex) {
            String output = ex.getMessage() + "\r\n";
            controller.appendOutput(output);
        }
    }

    private void rewriteConstants(List<kpi.skarlet.cad.lexer.lexemes.Constant> constants) {
        List<Constant> collect = constants.stream()
                .map(c -> new Constant(Float.valueOf(c.getName()), c.getType()))
                .collect(Collectors.toList());
        memory.getConstants().addAll(collect);
        this.conLimit = memory.getConstants().size() - 1;
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

    private void setVarSpCode() {
        for (Lexeme lexeme : lexemes) {
            int code = lexeme.getCode();
            if (code == getCode(TS.CONSTANT) || code == getCode(TS.IDENTIFIER)) {
                lexeme.setSpCode(lexeme.getSpCode() - 1);
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

    public void handleAll(List<Lexeme> lexemes) throws RuntimeExceptions {
        handleDeclarationList(lexemes);

        for (int i = lastOperatorIdx + 1; i < lexemes.size(); i++) {
            Lexeme lexeme = lexemes.get(i);
            int lexCode = lexeme.getCode();
//            handleOne(lexeme);
            if (lexCode == getCode(TS.IDENTIFIER) || lexeme.getCode() == getCode(TS.CONSTANT)) {
                stack.push(lexeme);
            } else if (lexCode == getCode(TS.LABEL)) {
                if (memory.getLabels().get(lexeme.getSpCodeLbl()).getToIdx() == i + 1)
                    continue;       // not push if it is label jump point
                stack.push(lexeme);
            } else if (lexCode != PC.SP_CODE) {    // operator
                if (lexCode == getCode(TS.EQUAL)) {
                    handleAssignment();
                } else if (isArithmeticSignCode(lexCode)) {
                    handleArithmeticOperation(lexCode);
                } else if (isLogicalSignCode(lexCode)) {
                    handleLogicalOperation(lexCode);
                }

            } else {    // lexeme.getCode() == PC.SP_CODE
                if (lexeme.getName().equals(PC.JMP)) {
                    int lblSpCode = stack.pop().getSpCodeLbl();
                    i = memory.getLabels().get(lblSpCode).getToIdx();
                } else if (lexeme.getName().equals(PC.JNE)) {
                    i = handleJNE(i);
                } else if (lexeme.getName().equals(PC.OUTPUT)) {
                    Lexeme pop = stack.pop();
                    float valF = valueOf(pop);
                    String output;
                    boolean typeInt = pop.getCode() == getCode(TS.IDENTIFIER) && memory.getVariables().get(pop.getSpCodeIdn()).getType().equals(VariableType.INT);
                    int valI = (int) valF;
                    if (typeInt) {
                        output = String.valueOf(valI);
                    } else {
                        output = String.valueOf(valF);
                    }
                    output += '\n';
                    controller.appendOutput(output);
                } else if (lexeme.getName().equals(PC.INPUT)) {
                    // future feature
                    controller.setInputPrompt("Enter " + stack.peek().getName());
                    Waiter waiter = new Waiter();
                    waiter.start();
                    try {
                        waiter.join();
                        controller.enterPressed = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    checkInput();
                } /*else if()*/
            }
        }
    }

    private int handleJNE(int i) {
        Lexeme o2 = stack.pop();    // second operand (label)
        Lexeme o1 = stack.pop();    // first operand (condition)
        if (o1.getSpCodeCon() != null) {
            // if true
            if (memory.getConstants().get(o1.getSpCode()).getValue() == 0f) {
                int lblSpCode = o2.getSpCodeLbl();      // (SpCodeLbl != null)?
                i = memory.getLabels().get(lblSpCode).getToIdx();
            }
        } else {
            System.err.println("Required CON code in condition constant!");
        }
        return i;
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
//                    throw new RuntimeExceptions();
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

    private float valueOf(Lexeme lexeme) throws RuntimeExceptions {
        if (lexeme.getCode() == getCode(TS.CONSTANT)) {
            return memory.getConstants().get(lexeme.getSpCodeCon()).getValue();
        } else if (lexeme.getCode() == getCode(TS.IDENTIFIER)) {
            Variable variable = memory.getVariables().get(lexeme.getSpCodeIdn());
            if (variable.getValue() == null)
                throw new VariableUsingException(variable.getName(), lexeme.getLine());
            return variable.getValue();
        } else {
            System.err.println("Found valueOf request of {Not CON & Not IDN}");
            return 0;
        }
    }

    private boolean isArithmeticSignCode(int code) {
        return code >= 24 && code <= 27;
    }

    private boolean isLogicalSignCode(int code) {
        return code >= 17 && code <= 22;
    }

    private void checkConstantDisposable(Lexeme o2) {
        if (o2.getSpCodeCon() != null && o2.getSpCode() > conLimit)
            memory.getConstants().remove(memory.getConstants().get(o2.getSpCode()));
    }

    private void handleArithmeticOperation(int opCode) throws RuntimeExceptions {
        Lexeme o2 = stack.pop();    // second operand
        Lexeme o1 = stack.pop();    // first operand
        String result = "-1";
        Lexeme res;
        if (opCode == getCode(TS.ASTERISK)) {
            result = String.valueOf(valueOf(o1) * valueOf(o2));
        } else if (opCode == getCode(TS.SLASH)) {
            result = String.valueOf(valueOf(o1) / valueOf(o2));
        } else if (opCode == getCode(TS.PLUS)) {
            result = String.valueOf(valueOf(o1) + valueOf(o2));
        } else if (opCode == getCode(TS.MINUS)) {
            result = String.valueOf(valueOf(o1) - valueOf(o2));
        }

        checkConstantDisposable(o2);
        checkConstantDisposable(o1);

        res = new Lexeme(result, o2.getLine(), getCode(TS.CONSTANT), memory.getConstants().size(), false);
        Constant constant = new Constant(Float.valueOf(result), VariableType.FLOAT);
        memory.getConstants().add(constant);
        stack.push(res);
    }

    private void handleLogicalOperation(int opCode) throws RuntimeExceptions {
        boolean twoOperand = true;
        Lexeme o2 = stack.pop();    // second operand
        Lexeme o1 = stack.pop();    // first operand
        String result = "-1";
        Lexeme res;
        if (opCode == getCode(TS.COMPARE)) {
            result = String.valueOf(valueOf(o1) == valueOf(o2) ? 1 : 0);
        } else if (opCode == getCode(TS.COMPARE_NEGATION)) {
            result = String.valueOf(valueOf(o1) != valueOf(o2) ? 1 : 0);
        } else if (opCode == getCode(TS.MORE)) {
            result = String.valueOf(valueOf(o1) > valueOf(o2) ? 1 : 0);
        } else if (opCode == getCode(TS.LESS)) {
            result = String.valueOf(valueOf(o1) < valueOf(o2) ? 1 : 0);
        } else if (opCode == getCode(TS.MORE_OR_EQUAL)) {
            result = String.valueOf(valueOf(o1) >= valueOf(o2) ? 1 : 0);
        } else if (opCode == getCode(TS.LESS_OR_EQUAL)) {
            result = String.valueOf(valueOf(o1) <= valueOf(o2) ? 1 : 0);
        } else if (opCode == getCode(TS.AND)) {
            result = String.valueOf(valueOf(o1) * valueOf(o2) == 0 ? 0 : 1);
        } else if (opCode == getCode(TS.OR)) {
            result = String.valueOf((valueOf(o1) != 0 || valueOf(o2) != 0) ? 1 : 0);
        } else if (opCode == getCode(TS.NEGATION)) {
            stack.push(o1);
            twoOperand = false;
            result = String.valueOf(valueOf(o2) == 0 ? 1 : 0);
        }

        checkConstantDisposable(o2);
        if (twoOperand) checkConstantDisposable(o1);

        res = new Lexeme(result, o2.getLine(), getCode(TS.CONSTANT), memory.getConstants().size(), false);
        Constant constant = new Constant(Float.valueOf(result), VariableType.INT);
        memory.getConstants().add(constant);
        stack.push(res);
    }

    private void handleAssignment() throws RuntimeExceptions {
        Lexeme o2 = stack.pop();    // second operand
        Lexeme o1 = stack.pop();    // first operand
        if (o1.getSpCodeIdn() != null) {
            Variable variable = memory.getVariables().get(o1.getSpCode());
            variable.setValue(valueOf(o2));
            checkConstantDisposable(o2);
        } else {
            System.err.println("Missing identifier special code! Assignment required a identifier as first operand");
        }
    }

    private void checkInput() {
        String inputString = controller.input;
        Lexeme idn = stack.pop();
        Float value = null;
        try {
            value = Float.parseFloat(inputString);
            memory.getVariables().get(idn.getSpCodeIdn()).setValue(value);
        } catch (NumberFormatException e) {
            System.err.println("Wrong input format");
            controller.appendOutput("Wrong number format! " + idn.getName() + " wasn't initialized");
        }
    }

    class Waiter extends Thread {
        @Override
        public void run() {
            System.out.println("Waiter run...");
            while (!controller.enterPressed) {
                try {
                    sleep(1000L);
                } catch (InterruptedException e) {

                }
            }
            System.out.println("Waiter stop");
        }
    }
}
