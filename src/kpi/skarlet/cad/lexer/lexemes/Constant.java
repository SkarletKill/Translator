package kpi.skarlet.cad.lexer.lexemes;

import kpi.skarlet.cad.lexer.VariableType;
import kpi.skarlet.cad.lexer.constants.CharacterConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class Constant {
    private static List<Constant> conList = new ArrayList<>();

    private String name;
    private VariableType type;

    public Constant(String name) {
        this.name = name;
        if (name.indexOf(CharacterConstants.dot) == -1) this.type = VariableType.INT;
        else this.type = VariableType.FLOAT;
    }

    /**
     * @return class Lexeme instance (if exists) / null
     */
    public static Constant get(String lex) {
        try {
            return conList.stream().filter(l -> lex.equals(l.name)).findAny().get();
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    public static boolean isExists(String lbl) {
        return Constant.get(lbl) != null;
    }

    public static int getCode(String con) {
        ArrayList<String> conNamesList = conList.stream().map(id -> id.name).collect(Collectors.toCollection(ArrayList::new));
        int code = conNamesList.indexOf(con);
        if (code == -1) code = conList.size();
        return code + 1;
    }

    public static List<Constant> getList() {
        return conList;
    }

    public Integer getId(){
        return conList.indexOf(this) + 1;
    }

    public String getName() {
        return name;
    }

    public VariableType getType() {
        return type;
    }
}
