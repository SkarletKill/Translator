package kpi.skarlet.cad.forwarding.constants;

public enum WordType {
    TERMINAL,
    NONTERMINAL;

    public static WordType getWordType(String type) {
        if (TERMINAL.name().toLowerCase().equals(type)) {
            return TERMINAL;
        } else if (NONTERMINAL.name().toLowerCase().equals(type)) {
            return NONTERMINAL;
        }
        return null;
    }
}
