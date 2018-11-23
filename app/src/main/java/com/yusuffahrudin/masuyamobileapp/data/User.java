package com.yusuffahrudin.masuyamobileapp.data;

/**
 * Created by yusuf fahrudin on 22-04-2017.
 */

public class User {
    private String user;
    private boolean view_cost;
    private String level, modul;
    private boolean akses, add, edit, delete, post;

    public void User(String user, boolean view_cost){
        this.user = user;
        this.view_cost = view_cost;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getModul() {
        return modul;
    }

    public void setModul(String modul) {
        this.modul = modul;
    }

    public boolean isAkses() {
        return akses;
    }

    public void setAkses(boolean akses) {
        this.akses = akses;
    }

    public boolean isAdd() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public boolean isPost() {
        return post;
    }

    public void setPost(boolean post) {
        this.post = post;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public boolean isView_cost() {
        return view_cost;
    }

    public void setView_cost(boolean view_cost) {
        this.view_cost = view_cost;
    }
}
