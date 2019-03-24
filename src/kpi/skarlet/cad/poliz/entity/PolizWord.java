package kpi.skarlet.cad.poliz.entity;

import kpi.skarlet.cad.forwarding.constants.WordType;
import kpi.skarlet.cad.forwarding.grammar.Word;
import kpi.skarlet.cad.poliz.constants.PolizWordType;

public class PolizWord extends Word {
    private PolizWordType type;

    public PolizWord(String name, PolizWordType type) {
        super(name, WordType.TERMINAL);
        this.type = type;
    }

    public PolizWordType getPWType() {
        return type;
    }

    public void setType(PolizWordType type) {
        this.type = type;
    }
}
