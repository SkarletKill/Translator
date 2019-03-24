package kpi.skarlet.cad.forwarding.grammar;

import kpi.skarlet.cad.forwarding.constants.WordType;

public class Word {
    private WordType type;
    private String name;

    public Word(String name) {
        this.name = name;
    }

    public Word(String name, WordType type) {
        this.type = type;
        this.name = name;
    }

    public WordType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setType(WordType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return (WordType.TERMINAL.equals(type)) ? name :
                new StringBuilder().append("<").append(name).append(">").toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Word word = (Word) o;

        return name.equals(word.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
