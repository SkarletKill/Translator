package kpi.skarlet.cad.poliz;

import kpi.skarlet.cad.forwarding.grammar.Word;
import kpi.skarlet.cad.poliz.constants.PolizWordType;
import kpi.skarlet.cad.poliz.entity.PolizTableElem;
import kpi.skarlet.cad.poliz.entity.PolizWord;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class PolizCalculator {
    private List<PolizTableElem> table;

    public PolizCalculator(List<Word> poliz) {
        this.table = new ArrayList<>();
        table.add(new PolizTableElem(PolizCalculator.this, poliz));
    }

    public void calculate() {
        List<Word> initPoliz = table.get(0).getPoliz();
        for (Word word : initPoliz) {
            word = (PolizWord) word;
            PolizTableElem elem = new PolizTableElem(this, table.get(table.size() - 1));
            if (((PolizWord) word).getPWType().equals(PolizWordType.IDENTIFIER) || ((PolizWord) word).getPWType().equals(PolizWordType.CONSTANT)) {
                elem.getStack().push(word);
            } else if (((PolizWord) word).getPWType().equals(PolizWordType.OPERATOR)) {
                Stack<Word> stack = elem.getStack();
                PolizWord w2 = (PolizWord) stack.pop();
                PolizWord w1 = (PolizWord) stack.pop();
                PolizWord polizWord;
                if (w1.getPWType().equals(PolizWordType.CONSTANT) && w2.getPWType().equals(PolizWordType.CONSTANT))
                    polizWord = new PolizWord(String.valueOf(makeOperation(w1, word, w2)), PolizWordType.CONSTANT);
                else
                    polizWord = new PolizWord(generateStr(w1.getName(), word.getName(), w2.getName()), PolizWordType.IDENTIFIER);
                stack.push(polizWord);
            }
            table.add(elem);
        }
    }

    private String generateStr(String... strings) {
        return "(" + String.join(" ", strings) + ")";
    }

    private double makeOperation(PolizWord w1, Word sign, PolizWord w2) {
        double v1 = Double.parseDouble(w1.getName());
        double v2 = Double.parseDouble(w2.getName());
        Double res = null;
        switch (sign.getName()) {
            case "+":
                res = v1 + v2;
                break;
            case "-":
                res = v1 - v2;
                break;
            case "*":
                res = v1 * v2;
                break;
            case "/":
                res = v1 / v2;
                break;
        }

        if (res == null) // unexpected sign
            return 0;
        return res;
    }

    public List<PolizTableElem> getTable() {
        return table;
    }
}
