package kpi.skarlet.cad.lexer.lexemes;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class Label {
    private static List<Label> lblList = new ArrayList<>();

    private String name;
    // indexes in lexemeList
    private Integer fromIndex;
    private Integer toIndex;

    public Label(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Integer getFromIndex() {
        return fromIndex;
    }

    public Integer getToIndex() {
        return toIndex;
    }

    /**
     * @return class Lexeme instance (if exists) / null
     */
    public static Label get(String lex) {
        try {
            return lblList.stream().filter(l -> lex.equals(l.name)).findAny().get();
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    public void setFrom(Integer from) {
        this.fromIndex = from;
    }

    public void setTo(Integer to) {
        this.toIndex = to;
    }

    public static boolean isExists(String lbl) {
        return Label.get(lbl) != null;
    }

    public static int getCode(String idn) {
        ArrayList<String> lblNamesList = lblList.stream().map(id -> id.name).collect(Collectors.toCollection(ArrayList::new));
        int code = lblNamesList.indexOf(idn);
        if (code == -1) code = lblList.size();
        return code + 1;
    }

    public Integer getId(){
        return lblList.indexOf(this) + 1;
    }

    public static List<Label> getList() {
        return lblList;
    }
}
