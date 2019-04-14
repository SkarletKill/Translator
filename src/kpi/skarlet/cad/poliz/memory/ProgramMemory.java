package kpi.skarlet.cad.poliz.memory;

import java.util.ArrayList;
import java.util.List;

public class ProgramMemory {
    private List<Variable> variables;
    private List<Label> labels;
    private List<Constant> constants;

    public ProgramMemory() {
        this.variables = new ArrayList<>();
        this.labels = new ArrayList<>();
        this.constants = new ArrayList<>();
    }

    public void clear() {
        this.variables.clear();
        this.labels.clear();
        this.constants.clear();
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public List<Constant> getConstants() {
        return constants;
    }
}
