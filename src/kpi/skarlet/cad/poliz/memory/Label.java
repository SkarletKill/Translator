package kpi.skarlet.cad.poliz.memory;

public class Label {
    private String name;
    private int fromIdx;
    private int toIdx;

    public Label(String name, int toIdx) {
        this.name = name;
        this.toIdx = toIdx;
    }

    public Label(int fromIdx, String name) {
        this.name = name;
        this.fromIdx = fromIdx;
    }

    public Label(int fromIdx, String name, int toIdx) {
        this.name = name;
        this.fromIdx = fromIdx;
        this.toIdx = toIdx;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFromIdx() {
        return fromIdx;
    }

    public void setFromIdx(int fromIdx) {
        this.fromIdx = fromIdx;
    }

    public int getToIdx() {
        return toIdx;
    }

    public void setToIdx(int toIdx) {
        this.toIdx = toIdx;
    }

    public boolean hasFromIdx() {
        return this.fromIdx != 0;
    }

    public boolean hasToIdx() {
        return this.toIdx != 0;
    }
}
