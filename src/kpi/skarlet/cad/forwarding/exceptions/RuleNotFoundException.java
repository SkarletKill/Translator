package kpi.skarlet.cad.forwarding.exceptions;

public class RuleNotFoundException extends TranslatorException {
    private int line;
    private String message;

    public RuleNotFoundException(int line) {
        this.line = line;
        this.message = "Rule not found";
    }

    @Override
    public String getMessage() {
        StringBuilder msgBuilder = new StringBuilder();
        if (line != 0) msgBuilder.append("line ")
                .append(line)
                .append(": ");
        msgBuilder.append("Syntax error! ").append(message);
        return msgBuilder.toString();
    }
}
