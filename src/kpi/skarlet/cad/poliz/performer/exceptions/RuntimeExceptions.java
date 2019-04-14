package kpi.skarlet.cad.poliz.performer.exceptions;

public class RuntimeExceptions extends Exception {
    protected String message;
    protected int line;

    public RuntimeExceptions(int line) {
        this.message = "Runtime Exception";
        this.line = line;
    }

    public RuntimeExceptions(String message, int line) {
        this.message = message;
        this.line = line;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
