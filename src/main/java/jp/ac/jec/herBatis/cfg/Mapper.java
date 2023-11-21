package jp.ac.jec.herBatis.cfg;

public class Mapper {

    /**
     * {@link jp.ac.jec.herBatis.annotation.Insert,jp.ac.jec.herBatis.annotation.Delete,jp.ac.jec.herBatis.annotation.Update,jp.ac.jec.herBatis.annotation.Select}
     */
    private Class<?> sqlTypeClass;
    private String querySQL;
    // 結果のクラス名
    private String resultClass;
    private String genericClass;

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

    public String getGenericClass() {
        return genericClass;
    }

    public void setGenericClass(String genericClass) {
        this.genericClass = genericClass;
    }
}
