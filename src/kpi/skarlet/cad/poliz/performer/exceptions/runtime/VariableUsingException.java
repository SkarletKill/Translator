package kpi.skarlet.cad.poliz.performer.exceptions.runtime;

import kpi.skarlet.cad.poliz.performer.exceptions.RuntimeExceptions;

public class VariableUsingException extends RuntimeExceptions {

    public VariableUsingException(int line) {
        super(line);
        generateMessage("");
    }

    public VariableUsingException(String varName, int line) {
        super(line);
        generateMessage(varName);
    }

    private void generateMessage(String varName) {
        StringBuilder msgBuilder = new StringBuilder();
        if (line != 0) msgBuilder.append("line ")
                .append(line)
                .append(": ")
                .append("Variable ");
        if (!varName.isEmpty()) msgBuilder.append("'").append(varName).append("' ");
        msgBuilder.append("using without initialization");
        this.message = msgBuilder.toString();
    }
}
