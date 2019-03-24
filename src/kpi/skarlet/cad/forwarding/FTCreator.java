package kpi.skarlet.cad.forwarding;

import kpi.skarlet.cad.forwarding.constants.WordType;
import kpi.skarlet.cad.forwarding.grammar.*;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FTCreator {
    public static final String PATH_GRAMMAR = "res/grammar.xml";
    public static final String PATH_RESULT_TABLE = "res/table.html";
    public static final String PATH_RESULT_GRAMMAR = "res/grammar.txt";
    private GrammarMap rules;
    private List<Word> columns;
    private Signs[][] forwardingTable;

    public static void main(String[] args) {
        FTCreator creator = new FTCreator();
        creator.writeSimpleGrammar(creator.rules.toString(), PATH_RESULT_GRAMMAR);
//        creator.showHtml();
        System.out.println();
    }

    public FTCreator() {
        GrammarReader grammarReader = new GrammarReader(PATH_GRAMMAR);
        rules = grammarReader.getRules();
        columns = createColuumns();
        forwardingTable = new Signs[columns.size()][columns.size()];
        for (int i = 0; i < forwardingTable.length; i++) {
            for (int j = 0; j < forwardingTable.length; j++) {
                forwardingTable[i][j] = new Signs();
            }
        }
        this.fillEqual();
        this.fillLess();
        this.fillMore();
        this.fillSharps();
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

    private int index(String word) {
        return getIndex(word, columns);
    }

    private void fillEqual() {
        for (Rule rule : rules) {
            List<RightSide> rights = rule.getRightSides();
            for (RightSide rs : rights) {
                List<String> r_words = rs.getWords().stream().map(Word::getName).collect(Collectors.toList());
                for (int i = 1; i < r_words.size(); i++) {
                    forwardingTable[index(r_words.get(i - 1))][index(r_words.get(i))].setEqual();
                }
            }
        }

    }

    private void fillLess() {
        List<Word[]> equalPairs = getEqualPairs();
        List<Word[]> pairs = equalPairs.stream().filter(p -> p[1].getType().equals(WordType.NONTERMINAL)).collect(Collectors.toList());
        // set less
        for (Word[] pair : pairs) {
            int word1 = columns.indexOf(pair[0]);
            List<Word> word2_firstPlus = firstPlus(pair[1]);
            for (Word w2 : word2_firstPlus) {
                forwardingTable[word1][columns.indexOf(w2)].setLess();
            }
        }
    }

    private void fillMore() {
        List<Word[]> equalPairs = getEqualPairs();
        List<Word[]> pairs = equalPairs.stream().filter(p -> p[0].getType().equals(WordType.NONTERMINAL)).collect(Collectors.toList());
        // set more
        for (Word[] pair : pairs) {
            int word2 = columns.indexOf(pair[1]);
            List<Word> word1_lastPlus = lastPlus(pair[0]);
            for (Word w1 : word1_lastPlus) {
                forwardingTable[columns.indexOf(w1)][word2].setMore();
            }
        }

        // де другий теж нетермінал
        pairs = pairs.stream().filter(p -> p[1].getType().equals(WordType.NONTERMINAL)).collect(Collectors.toList());
        for (Word[] pair : pairs) {
            List<Word> word1_lastPlus = lastPlus(pair[0]);
            List<Word> word2_firstPlus = firstPlus(pair[1]);
            for (Word w1 : word1_lastPlus) {
                for (Word w2 : word2_firstPlus) {
                    forwardingTable[columns.indexOf(w1)][columns.indexOf(w2)].setMore();
                }
            }
        }
        // ...
    }

    private void fillSharps() {
        for (int i = 0; i < forwardingTable.length - 1; i++) {
            forwardingTable[i][forwardingTable.length - 1].setMore();
            forwardingTable[forwardingTable.length - 1][i].setLess();
        }
    }

    private List<Word[]> getEqualPairs() {
        List<Word[]> wordPairs = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            for (int j = 0; j < columns.size(); j++) {
                if (forwardingTable[i][j].isEqual()) wordPairs.add(new Word[]
                        {
                                columns.get(i),
                                columns.get(j),
                        });
            }
        }
        return wordPairs;
    }

    public Signs getSignsBetween(Word w1, Word w2) {
        int row = columns.indexOf(w1);
        int col = columns.indexOf(w2);
        return (row >= 0 || col >= 0) ? forwardingTable[row][col] : null;
    }

    private List<Word> firstPlus(Word word) {
        List<Word> words = new ArrayList<>();
        return firstPlus(word, words);
    }

    private List<Word> firstPlus(Word word, List<Word> words) {
        if (word.getType().equals(WordType.TERMINAL)) return words;

        Rule rule = rules.get(word.getName());
        List<RightSide> rightSides = rule.getRightSides();
        for (RightSide rightSide : rightSides) {
            Word first = rightSide.getFirst();
            if (words.indexOf(first) == -1) words.add(first);
            else continue;
            if (first.getName().equals(word.getName())) continue;
            if (first.getType().equals(WordType.NONTERMINAL)) {
                List<Word> plusWords = firstPlus(first, words);
                plusWords.stream().filter(w -> words.indexOf(w) == -1).forEach(words::add);
            }
        }

        return words;
    }

    private List<Word> lastPlus(Word word) {
        List<Word> words = new ArrayList<>();
        return lastPlus(word, words);
    }

    private List<Word> lastPlus(Word word, List<Word> words) {
        if (word.getType().equals(WordType.TERMINAL)) return words;

        Rule rule = rules.get(word.getName());
        List<RightSide> rightSides = rule.getRightSides();
        for (RightSide rightSide : rightSides) {
            Word last = rightSide.getLast();
            if (words.indexOf(last) == -1) words.add(last);
            else continue;
            if (last.getName().equals(word.getName())) continue;
            if (last.getType().equals(WordType.NONTERMINAL)) {
                List<Word> plusWords = lastPlus(last, words);
                plusWords.stream().filter(w -> words.indexOf(w) == -1).forEach(words::add);
            }
        }

        return words;
    }

    private String getHtmlDoc() {
        StringBuilder builder = new StringBuilder();

        builder.append("<html>\n");
        builder.append("\t<head>\n");
        builder.append("\t<title>Forwarding table</title>\n");
        builder.append("\t<link rel=\"stylesheet\" type=\"text/css\" href=\"table_style.css\">\n");
        builder.append("\t</head>\n");
        builder.append("\t<body>\n");
        builder.append(getHtmlTable());
        builder.append("\t</body>\n");
        builder.append("</html>\n");

        return builder.toString();
    }

    private String getHtmlTable() {
        StringBuilder builder = new StringBuilder();

        builder.append("<table class=\"table\">\n");
        builder.append("    <tr class=\"table-header\">\n");

        // first col
        builder.append("        <th class=\"first_col\">");
        builder.append("\\");
        builder.append("</th>\n");

        // col names
        for (int i = 0; i < columns.size(); i++) {
            builder.append("        <th class=\"header__item\">");
            builder.append(columns.get(i).getName());
            builder.append("</th>\n");
        }
        builder.append("    </tr>\n");

        // data
        for (int y = 0; y < columns.size(); y++) {
            builder.append("    <tr class=\"table-row\">\n");
            // first col
            builder.append("        <td class=\"first_col\">");
            builder.append(columns.get(y).getName());
            builder.append("</td>\n");
            // data
            for (int x = 0; x < columns.size(); x++) {
                if (forwardingTable[y][x].hasConflict()) {
                    builder.append("        <td class=\"conflict-cell\">");
                } else {
                    builder.append("        <td class=\"table-data\">");
                }
                builder.append(forwardingTable[y][x].toString());
                builder.append("</td>\n");
            }
            builder.append("    </tr>\n");
        }
        builder.append("</table>");

        return builder.toString();
    }

    public static void writeSimpleGrammar(String text, String path) {
        if (path == null) path = PATH_RESULT_GRAMMAR;
        File file = new File(path);
        try {
            // запись всей строки
            FileWriter writer = new FileWriter(file, false);
            writer.write(" " + text.substring(1, text.length() - 1));
            writer.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void writeTable() {
        File file = new File(PATH_RESULT_TABLE);
        try {
            // запись всей строки
            FileWriter writer = new FileWriter(file, false);
            writer.write(getHtmlDoc());
            writer.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void showHtml() {
        File file = new File(PATH_RESULT_TABLE);
        try {
            writeTable();
            Desktop.getDesktop().browse(file.toURI());
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    }

    public GrammarMap getRules() {
        return rules;
    }
}