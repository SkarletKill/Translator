package kpi.skarlet.cad.forwarding;

public class Signs {
    private boolean less;
    private boolean equal;
    private boolean more;

    public boolean isLess() {
        return less;
    }

    public void setLess() {
        this.less = true;
    }

    public boolean isEqual() {
        return equal;
    }

    public void setEqual() {
        this.equal = true;
    }

    public boolean isMore() {
        return more;
    }

    public void setMore() {
        this.more = true;
    }

    public boolean hasConflict() {
        return (less & equal) || (less & more) || (equal & more);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (less) builder.append(" <");
        if (equal) builder.append(" =");
        if (more) builder.append(" >");
        return builder.toString();
    }
}
