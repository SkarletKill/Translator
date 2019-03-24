package kpi.skarlet.cad.forwarding.exceptions;

public abstract class TranslatorException extends Exception {
    public Integer getId() {
        return 1;
    }

    public abstract String getMessage();
}
