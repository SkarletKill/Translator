package kpi.skarlet.cad.forwarding.analyser;

import kpi.skarlet.cad.forwarding.FTCreator;
import kpi.skarlet.cad.forwarding.Signs;
import kpi.skarlet.cad.forwarding.constants.WordType;
import kpi.skarlet.cad.forwarding.exceptions.RatioNotExistsException;
import kpi.skarlet.cad.forwarding.exceptions.RuleNotFoundException;
import kpi.skarlet.cad.forwarding.grammar.RightSide;
import kpi.skarlet.cad.forwarding.grammar.Rule;
import kpi.skarlet.cad.forwarding.grammar.Word;
import kpi.skarlet.cad.lexer.LexicalAnalyser;
import kpi.skarlet.cad.lexer.exceptions.lexical.UnknownSymbolException;
import kpi.skarlet.cad.lexer.lexemes.Lexeme;
import kpi.skarlet.cad.poliz.entity.PolizWord;
import kpi.skarlet.cad.poliz.constants.PolizWordType;
import kpi.skarlet.cad.poliz.SSReader;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

public class FTAnalyser {
    private List<Lexeme> lexemes;
    private FTCreator creator;
    private SSReader ssReader;
    private TableStructure table;
    private Map<List<Word>, Word> reversedRules;
    private Map<List<Word>, Word> reversedPolizRules;
    private List<Word> basis;
    private List<Word> poliz;
    private Lexeme lastStackLexeme;

    public static void main(String[] args) {
        FTAnalyser analyser = new FTAnalyser();
        analyser.analyse();
    }

    public FTAnalyser() {
        LexicalAnalyser lexer = new LexicalAnalyser();
        lexer.run();
        lexemes = lexer.getLexemes();

        this.creator = new FTCreator();
        this.reversedRules = createReverseRulesMap();
        this.reversedPolizRules = createReversePolizRulesMap();
        this.table = new TableStructure(new ArrayList<>(), new ArrayList<>());
    }

    public FTAnalyser(LexicalAnalyser lexer, FTCreator creator, SSReader ssReader) {
        this.lexemes = lexer.getLexemes();
        this.creator = creator;
        this.ssReader = ssReader;
        this.reversedRules = createReverseRulesMap();
        this.table = new TableStructure(new ArrayList<>(), new ArrayList<>());
        this.reversedPolizRules = createReversePolizRulesMap();
        this.poliz = new ArrayList<>();
    }

    public void analyse() {
        this.clear();
        this.lexemes.add(0, new Lexeme(Sharp.name(), lexemes.get(0).getLine(), Sharp.code(), false));
        this.lexemes.add(new Lexeme(Sharp.name(), lexemes.get(lexemes.size() - 1).getLine(), Sharp.code(), false));
//        this.lexemes.add(new Lexeme(Sharp.name(), lexemes.get(lexemes.size() - 1).getLine(), Sharp.code()));

        this.table = new TableStructure(new ArrayList<>() {{
            add(new Word(lexemes.get(0).getName(), WordType.TERMINAL));
        }}, lexemes.subList(1, lexemes.size()));    // why?
        try {
            calculateRatio();
            while (!(table.getInput().size() == 1
                    && table.getStack().size() == 2
                    && table.getStack().get(1).getName().equals("program"))) {
                new TableElem(table, basis, poliz);
                processRatio(calculateRatio());
                calculateRatio();
            }
            new TableElem(table, basis, poliz);
            JOptionPane.showMessageDialog(null, "All right!");
            System.out.println("All right!");
        } catch (RuleNotFoundException | RatioNotExistsException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } catch (UnknownSymbolException e) {
            JOptionPane.showMessageDialog(null, "Lexical exception! " + e.getMessage());
        }

        for (TableElem tableElem : TableElem.getParseTable()) {
            System.out.println(tableElem);
        }
    }

    private Signs calculateRatio() throws UnknownSymbolException {
        Signs ratio = creator.getSignsBetween(table.getStackLast(), table.getInputFirstWord());
        if (ratio == null) throw new UnknownSymbolException(table.getInputFirstWord().getName().charAt(0), 0);
        table.setRatio(ratio);
        return ratio;
    }

    private void processRatio(Signs ratio) throws RuleNotFoundException, RatioNotExistsException {
        basis = null;
        if (ratio.isLess() || ratio.isEqual()) {
            lastStackLexeme = table.getInputFirst();
            table.moveWordFromInputToStack();
        } else if (ratio.isMore()) {
            replaceByRule(findLastLessSign());
        } else {
            System.err.println("Ratio is empty!");
            List<Word> stack = table.getStack();
            throw new RatioNotExistsException(table.getInput().size() > 0 ? table.getInput().get(0).getLine()
                    : lexemes.get(lexemes.size() - 1).getLine(),
                    stack.get(stack.size() - 1).getName(),
                    table.getInput().get(0).getName());
        }
    }

    private int findLastLessSign() {
        int indexLess = -1;
        for (int i = table.getStack().size() - 1; i > 0; i--) {
            Signs sign = creator.getSignsBetween(table.getStack().get(i - 1), table.getStack().get(i));
            if (sign.isEqual()) continue;
            if (sign.isMore()) {
                System.err.println("Find second 'MORE' sign");
                break;
            }
            if (sign.isLess()) {
                indexLess = i;
                break;
            }
        }

        return indexLess;
    }

    private List<Word> findRightSide(List<Word> wordList) {
        outer:
        for (List<Word> words : reversedRules.keySet()) {
            if (words.size() != wordList.size()) continue;
            for (int i = 0; i < words.size(); i++) {
                if (!words.get(i).equals(wordList.get(i))) continue outer;
            }
            return words;
        }
        return null;
    }

    private Word findRule(List<Word> words) {
//        return reversedRules.get(findRightSide(words));
        return reversedRules.get(words);
    }

    private Word findPolizRule(List<Word> words) {
//        return reversedRules.get(findRightSide(words));
        return reversedPolizRules.get(words);
    }

    private Map<List<Word>, Word> createReverseRulesMap() {
        Map<List<Word>, Word> reverseMap = new HashMap<>();
        for (Rule rule : creator.getRules()) {
            List<List<Word>> collect = rule.getRightSides().stream().map(RightSide::getWords).collect(Collectors.toList());
            for (List<Word> wordList : collect) {
                reverseMap.put(wordList, new Word(rule.getName(), WordType.NONTERMINAL));
            }
        }
        return reverseMap;
    }

    private Map<List<Word>, Word> createReversePolizRulesMap() {
        Map<List<Word>, Word> reverseMap = new HashMap<>();
        for (Rule rule : ssReader.getRules()) {
            List<List<Word>> collect = rule.getRightSides().stream().map(RightSide::getWords).collect(Collectors.toList());
            for (List<Word> wordList : collect) {
                reverseMap.put(wordList, new Word(rule.getName(), WordType.NONTERMINAL));
            }
        }
        return reverseMap;
    }

    private void replaceByRule(int indexLess) throws RuleNotFoundException {
        if (indexLess == -1) return;
        List<Word> wordsForReplace = table.getStack()
                .subList(indexLess, table.getStack().size());
        basis = new ArrayList<>();
        basis.addAll(wordsForReplace);
        Word replaced = findRule(wordsForReplace);

        Word polizReplaced = findPolizRule(wordsForReplace);
        if (polizReplaced != null) {
            polizReplaced.setType(WordType.TERMINAL);
            switch (polizReplaced.getName()) {
                case "identifier":
                    poliz.add(new PolizWord(lastStackLexeme.getName(), PolizWordType.IDENTIFIER));
                    break;
                case "constant":
                    poliz.add(new PolizWord(lastStackLexeme.getName(), PolizWordType.CONSTANT));
                    break;
                default:
                    poliz.add(new PolizWord(polizReplaced.getName(), PolizWordType.OPERATOR));
                    break;
            }
        }

        if (replaced == null) {
            System.err.println("replaceByRule => rule not found!");
            throw new RuleNotFoundException(table.getInput().size() > 0 ? table.getInput().get(0).getLine()
                    : lexemes.get(lexemes.size() - 1).getLine());
        }

        table.cutStackTail(indexLess);
        table.getStack().add(replaced);
    }

    public void clear() {
//        creator.clear();
        try {
            table.clear();
        } catch (ConcurrentModificationException e){
            // ignore
            e.printStackTrace();
        }
        TableElem.getParseTable().clear();
        poliz.clear();
    }

    public List<Word> getPoliz() {
        return poliz;
    }
}