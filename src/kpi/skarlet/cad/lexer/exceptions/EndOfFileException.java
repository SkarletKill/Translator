package kpi.skarlet.cad.lexer.exceptions;

import java.io.IOException;

public class EndOfFileException extends IOException {
    private String message = "End of file reached!";
    private StackTraceElement lastState;

    public EndOfFileException(StackTraceElement lastState) {
        this.lastState = lastState;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getLastState() {
        return lastState.getMethodName();
    }
}
