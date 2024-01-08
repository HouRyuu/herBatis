package jp.ac.jec.herBatis.cfg;

import java.util.List;

public class IfOGNL {

    private String testOgnl;
    private String sql;
    private List<Foreach> foreachList;

    public IfOGNL(String testOgnl, String sql) {
        this.testOgnl = testOgnl;
        this.sql = sql;
    }

    public String getTestOgnl() {
        return testOgnl;
    }

    public void setTestOgnl(String testOgnl) {
        this.testOgnl = testOgnl;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<Foreach> getForeachList() {
        return foreachList;
    }

    public void setForeachList(List<Foreach> foreachList) {
        this.foreachList = foreachList;
    }
}
