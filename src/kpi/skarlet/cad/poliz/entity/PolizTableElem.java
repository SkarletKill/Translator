package kpi.skarlet.cad.poliz.entity;

import kpi.skarlet.cad.forwarding.grammar.Word;
import kpi.skarlet.cad.poliz.PolizCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class PolizTableElem {
    private PolizCalculator parent;
    private Stack<Word> stack;
    private List<Word> poliz;

    public PolizTableElem(PolizCalculator parent) {
        this.parent = parent;
        this.stack = new Stack<>();
        this.poliz = new ArrayList<>();
    }

    public PolizTableElem(PolizCalculator parent, PolizTableElem elem) {
        this.parent = parent;
        this.stack = new Stack<>();
        this.stack.addAll(elem.getStack());
        this.poliz = new ArrayList<>();
        this.poliz.addAll(elem.getPoliz().subList(1, elem.getPoliz().size()));
    }

    public PolizTableElem(PolizCalculator parent, List<Word> poliz) {
        this.parent = parent;
        this.stack = new Stack<>();
        this.poliz = poliz;
    }

    public Stack<Word> getStack() {
        return stack;
    }

    public List<Word> getPoliz() {
        return poliz;
    }

    public Integer getId() {
        return parent.getTable().indexOf(this) + 1;
    }

    public String getStrStack() {
        return stack.toString();
    }

    public String getStrPoliz() {
        return poliz.toString();
    }


}
