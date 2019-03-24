package kpi.skarlet.cad.forwarding.grammar;

import kpi.skarlet.cad.forwarding.constants.WordType;

import java.util.ArrayList;
import java.util.List;

public class RightSide {
    private List<Word> words;

    public RightSide() {
        words = new ArrayList<>() {
            @Override
            public String toString() {
                StringBuilder result = new StringBuilder();
                for (Word word : this) {
                    result.append(" ");
                    result.append(word);
                }
                return result.toString().trim();
            }
        };
    }

    public List<Word> getWords() {
        return words;
    }

    public void addWord(Word word) {
        words.add(word);
    }

    public void addWord(String name, WordType type) {
        words.add(new Word(name, type));
    }

    public void addWords(List<Word> words) {
        this.words.addAll(words);
    }

    public Word getFirst() {
        return (words.isEmpty()) ? null : words.get(0);
    }

    public Word getLast() {
        return (words.isEmpty()) ? null : words.get(words.size() - 1);
    }

    @Override
    public String toString() {
        return words.toString();
    }
}
