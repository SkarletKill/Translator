package kpi.skarlet.cad.poliz.memory;

import kpi.skarlet.cad.lexer.lexemes.Lexeme;

import java.util.ArrayList;
import java.util.List;

public class ProgramMemory {
    private List<Variable> variables;
    private List<Label> labels;

    public ProgramMemory() {
        this.variables = new ArrayList<>();
        this.labels = new ArrayList<>();
    }

    public void clear() {
        this.variables.clear();
        this.labels.clear();
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public List<Label> getLabels() {
        return labels;
    }
}
