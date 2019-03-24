package kpi.skarlet.cad.lexer.lexemes;

import kpi.skarlet.cad.lexer.VariableType;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class Identifier {
    private static List<Identifier> idnList = new ArrayList<>();

    private VariableType type;
    private String name;
//    private String value;

    public Identifier(VariableType type, String name) {
        this.type = type;
        this.name = name;
    }

    public static boolean isExists(String idn) {
        return Identifier.get(idn) != null;
    }

    public static int getCode(String idn) {
        ArrayList<String> idnNamesList = idnList.stream().map(id -> id.name).collect(Collectors.toCollection(ArrayList::new));
        int code = idnNamesList.indexOf(idn);
        if (code == -1) code = idnList.size();
        return code + 1;
    }

    public static List<Identifier> getList() {
        return idnList;
    }

    /**
     * @return class Lexeme instance (if exists) / null
     */
    public static Identifier get(String lex) {
        try {
            return idnList.stream().filter(l -> lex.equals(l.name)).findAny().get();
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    public Integer getId(){
        return idnList.indexOf(this) + 1;
    }

    public String getName() {
        return name;
    }

    public VariableType getType() {
        return type;
    }
}
