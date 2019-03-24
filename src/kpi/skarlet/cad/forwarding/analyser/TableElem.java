package kpi.skarlet.cad.forwarding.analyser;

import kpi.skarlet.cad.forwarding.Signs;
import kpi.skarlet.cad.forwarding.grammar.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TableElem {
    private static List<TableElem> parseTable = new ArrayList<>();
    private String stack;
    private Signs ratio;
    private String input;
    private String basis;
    private List<Word> poliz;

    public TableElem(TableStructure table) {
        this.stack = table.getStack().toString();
        this.ratio = table.getRatio();
        this.input = table.getInput().toString();
        this.poliz = new ArrayList<>();

        parseTable.add(this);
    }

    public TableElem(TableStructure table, List<Word> basis) {
        this(table);
        this.basis = (basis != null) ?
                String.join(", ", basis.stream().map(Word::toString).collect(Collectors.toList())) :
                "";
    }

    public TableElem(TableStructure table, List<Word> basis, List<Word> poliz) {
        this(table, basis);
        this.poliz.addAll(poliz);
    }

    public Integer getId() {
        return parseTable.indexOf(this) + 1;
    }

    public String getStack() {
        return stack;
    }

    public String getRatio() {
        return ratio.toString();
    }

    public String getInput() {
        return input;
    }

    public String getBasis() {
        return basis;
    }

    public List<Word> getPoliz() {
        return poliz;
    }

    public static List<TableElem> getParseTable() {
        return parseTable;
    }

    @Override
    public String toString() {
        return "TableElem{" +
                "stack='" + stack + '\'' +
                ", ratio=" + ratio +
                ", input='" + input + '\'' +
                ", basis='" + basis + '\'' +
                '}';
    }
}
