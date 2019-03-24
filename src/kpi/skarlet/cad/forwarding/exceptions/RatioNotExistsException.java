package kpi.skarlet.cad.forwarding.exceptions;

public class RatioNotExistsException extends TranslatorException {
    private int line;
    private String message;

    public RatioNotExistsException(int line, String stackHead, String firstInputElem) {
        this.line = line;
        this.message = "Ratio is not exists between \'" + stackHead + "\' and \'" + firstInputElem + "\'";
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
