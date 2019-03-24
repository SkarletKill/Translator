package kpi.skarlet.cad.forwarding.grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Rule {
    private String name;
    private List<RightSide> rightSides;

    public Rule(String name) {
        this.name = name;
        this.rightSides = new ArrayList<>() {
            @Override
            public String toString() {
                return this.stream()
                        .map(RightSide::toString)
                        .collect(Collectors.joining(" | "));
            }
        };
    }

    public void addRightSide(RightSide rs) {
        rightSides.add(rs);
    }

    public void addRightSide(List<Word> words) {
        rightSides.add(new RightSide() {
            {
                this.addWords(words);
            }
        });
    }

    public List<RightSide> getRightSides() {
        return rightSides;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "<" + this.getName() + "> ::= " + this.getRightSides().toString();
    }
}
