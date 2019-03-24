package kpi.skarlet.cad.forwarding.grammar;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class GrammarMap extends ArrayList<Rule> {
    public Rule get(String name) {
        int index = this.indexOf(name);
        if (index == -1) return null;
        return this.get(index);
    }

    public int indexOf(String s) {
        if (s == null) {
            for (int i = 0; i < this.size(); i++)
                if (this.get(i).getName() == null)
                    return i;
        } else {
            for (int i = 0; i < this.size(); i++)
                if (s.equals(this.get(i).getName()))
                    return i;
        }
        return -1;
    }

    @Override
    public String toString() {
        return this.stream()
                .map(Rule::toString)
                .collect(Collectors.joining("\n ", "{", "}"));
    }
}
