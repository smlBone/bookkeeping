package org.mf.bookkeeping.models;


public class User {
    private int id;
    private String name;
    private int account;
    private String password;
    private String portraitPath;

    public User() {}

    public User(String name, int account, String password) {
        this.name = name;
        this.account = account;
        this.password = password;
    }

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

    public int getAccount() {
        return account;
    }

    public void setAccount(int account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPortraitPath() {
        return this.portraitPath;
    }

    public void setPortraitPath(String portraitPath) {
        this.portraitPath = portraitPath;
    }

    @Override
    public String toString() {
        return "用户名: " + name +
                "账号: " + account +
                "密码: " + password;
    }
}