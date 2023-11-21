package jp.ac.jec.herBatis.cfg;


import java.util.HashMap;
import java.util.Map;

public class Configuration {

    private String driver;
    private String url;
    private String username;
    private String password;
    private boolean autoCommit = true;

    private Map<String, Mapper> mapperMap = new HashMap<>();


    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public void setMapperMap(Map<String, Mapper> mappers) {
        this.mapperMap.putAll(mappers);
    }

    public Map<String, Mapper> getMapperMap() {
        return mapperMap;
    }
}
