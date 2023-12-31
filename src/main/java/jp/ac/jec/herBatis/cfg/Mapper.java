package jp.ac.jec.herBatis.cfg;

import java.util.ArrayList;
import java.util.List;

public class Mapper {

    /**
     * {@link jp.ac.jec.herBatis.annotation.Insert,jp.ac.jec.herBatis.annotation.Delete,jp.ac.jec.herBatis.annotation.Update,jp.ac.jec.herBatis.annotation.Select}
     */
    private Class<?> sqlTypeClass;
    private String querySQL;
    // 結果のクラス名
    private String resultClass;

    private final List<Object> params = new ArrayList<>();
    private List<IfOGNL> ifList;
    private List<Foreach> foreachList;

    public Class<?> getSqlTypeClass() {
        return sqlTypeClass;
    }

    public void setSqlTypeClass(Class<?> sqlTypeClass) {
        this.sqlTypeClass = sqlTypeClass;
    }

    public String getQuerySQL() {
        return querySQL;
    }

    public void setQuerySQL(String querySQL) {
        this.querySQL = querySQL;
    }

    public String getResultClass() {
        return resultClass;
    }

    public void setResultClass(String resultClass) {
        this.resultClass = resultClass;
    }

    public void addParam(Object param) {
        this.params.add(param);
    }

    public List<Object> getParams() {
        return params;
    }

    public List<IfOGNL> getIfList() {
        return ifList;
    }

    public void setIfList(List<IfOGNL> ifList) {
        this.ifList = ifList;
    }

    public List<Foreach> getForeachList() {
        return foreachList;
    }

    public void setForeachList(List<Foreach> foreachList) {
        this.foreachList = foreachList;
    }
}
