package kpi.skarlet.cad.poliz;

import kpi.skarlet.cad.forwarding.FTCreator;
import kpi.skarlet.cad.forwarding.constants.WordType;
import kpi.skarlet.cad.forwarding.grammar.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SSReader {
    public static final String PATH_GRAMMAR = "res/semantic_subroutine.xml";
    public static final String PATH_RESULT_GRAMMAR = "res/semantic_subroutine.txt";
    private GrammarMap rules;
    private List<Word> columns;

    public static void main(String[] args) {
        SSReader ssReader = new SSReader();
        FTCreator.writeSimpleGrammar(ssReader.rules.toString(), PATH_RESULT_GRAMMAR);
        System.out.println();
    }

    public SSReader() {
        GrammarReader grammarReader = new GrammarReader(PATH_GRAMMAR);
        rules = grammarReader.getRules();
        columns = createColuumns();
    }

    public List<Word> createColuumns() {
        List<Word> cols = new ArrayList<>() {
            @Override
            public int indexOf(Object o) {
                for (int i = 0; i < this.size(); i++) {
                    if (this.get(i).getName().equals(((Word) o).getName())) return i;
                }
                return super.indexOf(o);
            }
        };

        for (Rule rule : rules) {
            cols.add(new Word(rule.getName(), WordType.NONTERMINAL));
        }
        for (Rule rule : rules) {
            List<RightSide> rights = rule.getRightSides();
            for (RightSide rs : rights) {
                List<Word> r_words = rs.getWords();
                for (Word r_word : r_words) {
                    if (getIndex(r_word.getName(), cols) == -1) {
                        cols.add(new Word(r_word.getName(), r_word.getType()));
                    }
                }
//                if (getIndex(rule.getName(), cols) != -1) {
//                    cols.add()
//                }
            }

        }
        cols.add(new Word("#", WordType.NONTERMINAL));
        return cols;
    }

    private int getIndex(String s, List<Word> list) {
        if (s == null) {
            for (int i = 0; i < list.size(); i++)
                if (list.get(i).getName() == null)
                    return i;
        } else {
            for (int i = 0; i < list.size(); i++)
                if (s.equals(list.get(i).getName()))
                    return i;
        }
        return -1;
    }

    public GrammarMap getRules() {
        return rules;
    }
}
