package jp.ac.jec.herBatis.cfg;

public class Foreach {

    private String collection;
    private String open;
    private String close;
    private String separator;

    public Foreach(String collection, String open, String close, String separator) {
        this.collection = collection;
        this.open = open;
        this.close = close;
        this.separator = separator;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }
}
