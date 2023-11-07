package jp.ac.jec.entity;

import jp.ac.jec.herBatis.annotation.Table;

@Table("t_user")
public class UserPO {

    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
