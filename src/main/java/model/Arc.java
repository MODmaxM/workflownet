package model;

public class Arc {
    private String source;
    private String end;

    public Arc(String source,String end){
        this.source=source;
        this.end=end;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
